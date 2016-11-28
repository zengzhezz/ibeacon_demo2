//定义一个保存现有beacon id的数组
var beaconID = new Array();
var socket = null;

window.onload = function(){
  //操作socket
  socket = new WebSocket('ws://localhost:8080/ibeacon_demo2/websocket');
  socket.onopen = function(event){
	  //发送一个初始化消息
	  socket.send("I am a client and I am listening!");
  };
  socket.onmessage = function(event){
	  console.log('client has received a message.',event);
  };
  
  //操作原点
  var origin = document.getElementById('origin-dot');
  origin.onmousedown = oriDown;

  var button = document.getElementById('mybutton');
  button.onclick = function(){
    var input = document.getElementById('beaconid');
    var beaconid = input.value;
    if(beaconid!=""){
        addBeacon(beaconid);
    }else{
      alert("id不能为空");
    }
  };

}

//添加beacon点
function addBeacon(id){
    if(beaconID.indexOf(id) === -1){
        beaconID.push(id);
        var beacon = document.createElement("div"),
            text = document.createElement("p");
        text.textContent = id;
        beacon.setAttribute("id",id);
        beacon.appendChild(text);
        beacon.style.textAlign = "center";
        beacon.style.verticalAlign = "center";
        beacon.style.width = "100px";
        beacon.style.height = "100px";
        beacon.style.lineHeight = "100px";
        beacon.style.position = "absolute";
        beacon.style.left = "300px";
        beacon.style.top = "300px";
        beacon.style.borderRadius = "50px";
        beacon.style.cursor = "move";
        beacon.onmousedown = fnDown;
        beacon.style.backgroundColor = "#27e7b3";
        document.body.appendChild(beacon);
        addBeaconMsg(id);
        //向服务器发送添加信息
        socket.send(getMsgString("add",id,beacon.offsetLeft,beacon.offsetTop));
    }else{
        alert("id已存在");
    }

}

//添加beacon相关的显示信息
function addBeaconMsg(name){
    var msg = document.createElement("div"),
        show_name = document.createElement("p"),
        show_x = document.createElement("p"),
        show_y = document.createElement("p");
    var box = document.getElementById('msg_box'),
        beacon = document.getElementById(name);
    msg.style.border = '2px solid #e6e92f';
    msg.style.textAlign = "center";
    msg.style.padding = "5px";
    msg.style.margin = "5px";
    show_name.textContent = name;
    show_name.style.padding = "2px";
    show_x.style.padding = "2px";
    show_y.style.padding = "2px";
    show_name.style.fontSize = "large";
    show_x.style.fontSize = "large";
    show_y.style.fontSize = "large";
    show_x.textContent = "x : " + beacon.offsetLeft;
    show_y.textContent = "y : " + beacon.offsetTop;
    msg.setAttribute("class", name);
    msg.appendChild(show_name);
    msg.appendChild(show_x);
    msg.appendChild(show_y);
    box.appendChild(msg);
}

//鼠标按下时的操作
function fnDown(event){
	//获取该beacon
	var beacon = this;
    var id = this.id,
        // 光标按下时光标和面板之间的距离
        disX=event.clientX-this.offsetLeft,
        disY=event.clientY-this.offsetTop;
    document.onmousemove = function(event){
      fnMove(event,disX,disY,id);
    };
    document.onmouseup = function(){
    	//向服务器发送更新位置信息
    	socket.send(getMsgString("update",id,beacon.offsetLeft,beacon.offsetTop));
    	document.onmousemove = null;
    	document.onmouseup = null;
    };
}

function fnMove(event,posX,posY,id){
  var oDrag = document.getElementById(id),
      left = event.clientX - posX,
      top = event.clientY - posY;
  oDrag.style.left = left + 'px';
  oDrag.style.top = top + 'px';
  var msg = document.getElementsByClassName(id);
  msg[0].childNodes[1].textContent = "x : " + oDrag.offsetLeft;
  msg[0].childNodes[2].textContent = "y : " + oDrag.offsetTop;
}

function getMsgString(operation, id, x, y){
	var msg = operation + ',' + id + ',' + x + ',' + y;
	return msg;
}

//按下原点时的操作
function oriDown(event){
	// 光标按下时光标和面板之间的距离
	var ori = this;
    disX=event.clientX-this.offsetLeft,
    disY=event.clientY-this.offsetTop;
    document.onmousemove = function(event){
    	var left = event.clientX - disX,
        	top = event.clientY - disY;
    	ori.style.left = left + 'px';
    	ori.style.top = top + 'px';
    };
    document.onmouseup = function(){
    	document.onmousemove = null;
    	document.onmouseup = null;
    };
}
