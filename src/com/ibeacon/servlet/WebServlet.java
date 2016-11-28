package com.ibeacon.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.ibeacon.model.Beacon;

@ServerEndpoint("/websocket")
public class WebServlet {
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    
    //定义一个全局List来保存前端传来的Beacon数据
    public static List<Beacon> beaconList = new ArrayList<Beacon>();
     
    //concurrent包的线程安全Set，用来存放每个客户端对应的DemoWebServlet对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<WebServlet> webSocketSet = new CopyOnWriteArraySet<WebServlet>();
     
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
     
    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }
     
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(){
    	beaconList.clear();
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1    
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }
     
    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        parseMsg(message);
        showBeaconList();
        //群发消息
        for(WebServlet item: webSocketSet){             
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
     
    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误");
        error.printStackTrace();
    }
     
    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }
 
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
 
    public static synchronized void addOnlineCount() {
    	WebServlet.onlineCount++;
    }
     
    public static synchronized void subOnlineCount() {
    	WebServlet.onlineCount--;
    }
    
    /**
     * 解析前端传来的数据，message格式为(operate,id,x,y)，分别对应(操作,id,x坐标,y坐标)
     * @param message
     */
    public void parseMsg(String message){
    	String[] msg = message.split(",");
    	switch(msg[0]){
    		case "add": 
    			Beacon beacon = new Beacon();
    			beacon.setId(msg[1]);
    			beacon.setX(Double.parseDouble(msg[2]));
    			beacon.setY(Double.parseDouble(msg[3]));
    			beaconList.add(beacon);
    			break;
    		case "update":
    			for (Beacon b : beaconList) {
					if(b.getId().equals(msg[1])){
						b.setX(Double.parseDouble(msg[2]));
						b.setY(Double.parseDouble(msg[3]));
						beaconList.set(beaconList.indexOf(b), b);
					}
				}
    			break;
    		default:
    			break;
    	}
    }
    
    public void showBeaconList(){
    	for (Beacon beacon : beaconList) {
			System.out.println("id="+beacon.getId()+" x="+beacon.getX()+" y="+beacon.getY());
		}
    }
}
