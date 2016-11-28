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
	//��̬������������¼��ǰ������������Ӧ�ð�����Ƴ��̰߳�ȫ�ġ�
    private static int onlineCount = 0;
    
    //����һ��ȫ��List������ǰ�˴�����Beacon����
    public static List<Beacon> beaconList = new ArrayList<Beacon>();
     
    //concurrent�����̰߳�ȫSet���������ÿ���ͻ��˶�Ӧ��DemoWebServlet������Ҫʵ�ַ�����뵥һ�ͻ���ͨ�ŵĻ�������ʹ��Map����ţ�����Key����Ϊ�û���ʶ
    private static CopyOnWriteArraySet<WebServlet> webSocketSet = new CopyOnWriteArraySet<WebServlet>();
     
    //��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������
    private Session session;
     
    /**
     * ���ӽ����ɹ����õķ���
     * @param session  ��ѡ�Ĳ�����sessionΪ��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);     //����set��
        addOnlineCount();           //��������1
        System.out.println("�������Ӽ��룡��ǰ��������Ϊ" + getOnlineCount());
    }
     
    /**
     * ���ӹرյ��õķ���
     */
    @OnClose
    public void onClose(){
    	beaconList.clear();
        webSocketSet.remove(this);  //��set��ɾ��
        subOnlineCount();           //��������1    
        System.out.println("��һ���ӹرգ���ǰ��������Ϊ" + getOnlineCount());
    }
     
    /**
     * �յ��ͻ�����Ϣ����õķ���
     * @param message �ͻ��˷��͹�������Ϣ
     * @param session ��ѡ�Ĳ���
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("���Կͻ��˵���Ϣ:" + message);
        parseMsg(message);
        showBeaconList();
        //Ⱥ����Ϣ
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
     * ��������ʱ����
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("��������");
        error.printStackTrace();
    }
     
    /**
     * ������������漸��������һ����û����ע�⣬�Ǹ����Լ���Ҫ��ӵķ�����
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
     * ����ǰ�˴��������ݣ�message��ʽΪ(operate,id,x,y)���ֱ��Ӧ(����,id,x����,y����)
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
