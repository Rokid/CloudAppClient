## Cloud App 开发协议
### Rokid 开放平台
版本：1.0.0-alpha

### 大纲
* [简介](#1-简介)
	* [一些概念](#11-一些概念)
* [Request](#2-request)
	* [协议概览](#21-协议概览)
	* [Session定义](#22-session定义)
	* [Context定义](#23-context定义)
	* [Request定义](#24-request定义)
* [Response](#3-response)
	* [协议概览](#31-协议概览)
	* [Action定义](#32-action定义)

### 1. 简介

本文是对在*[Rokid开放平台][developer_website_link]*上开发CloudApp的协议的详细描述。

[developer_website_link]: https://developer.rokid.com

#### 1.1 一些概念

在了解本文所描述协议之前，需要对一下概念作如下说明：

* **CloudApp** - 在*[Rokid开放平台][developer_website_link]*上接入的云端应用，可以理解为遵循本文所描述的协议开发的某种云端服务或小应用。
* **CloudDispatcher** - 用于向CloudApp传递请求和分发CloudApp返回结果的模块。
* **CloudAppClient** - 用于处理CloudApp返回结果的设备端的执行容器。
* **RokidMobileSDK** - 与*[Rokid开放平台][developer_website_link]*向关联的手机端SDK，用于对CloudApp的信息扩展展示或第三方授权。
* **TTS** - **T**ext **T**o **S**peech的缩写，这是机器人的语音表达方式。

### 2. Request

*Request* is used to fetch response from *CloudApps*, which is sent by *CloudDispatcher*. Currently **IntentRequest** and **EventRequest** are available. **IntentRequest** is created according an *NLP* intent. And **EventRequest** is created when an event occurs.

*Request* 是*CloudDispatcher*产生的用于向*CloudApp*获取对应返回结果的请求。目前有两种类型的请求：一种是**IntentRequest**，一种是**EventRequest**。**IntentRequest** 是根据语音识别和语义理解（*NLP*）的结果创建的，其中会带有（*NLP*）的信息。**EventRequest**是在当有某种事件发生是产生的，并通过*CloudDispatcher*转发给当前*CloudApp*，比如当某个TTS播放结束的时候会产生一个TTS结束的事件，当前*CloudApp*可以选择处理或者不处理。

#### 2.1 协议概览

*Request* 的整体协议定义如下所示：

```
{

    "version": "2.0.0",

    "session": {

        "sessionId": "D75D1C9BECE045E9AC4A87DA86303DD6", 

        "newSession": true, 

        "attributes": {
            "key1": "value1",
	    "key2": "value2"
        }
	
    },

    "context": {

        "application": {
	    "applicationId": "application id for requested CloudApp"
        },

        "device": {  
	    
	    "basic":{
                "vendor":"vendor id",
                "deviceType":"device type",
                "deviceId": "010116000100",
                "locale": "zh-cn",
                "timestamp": 1478009510909
	    },

            "screen":{
		"x":"640",
		"y":"480"
            },

            "media": {
                "state": "PLAYING / PAUSED / IDLE"
            },
            "voice": {
                "state": "PLAYING / PAUSED / IDLE"
            },
            "location": {
                "latitude": "30.213322455923485",
                "longitude": "120.01190010997654"
            }
	    
        },

        "user": {
            "userId": "user id string"
        }
	
    },

    "request": {

        "reqType": "intent / event",

        "reqId": "010116000100-ad1f462f4f0946ccb24e9248362c504a",

	"content": {
            "applicationId": "com.rokid.cloud.music",
            "intent": "play_random",
            "slots": {
            }
        }
	
    }
    
}
```

* **request** - 协议中真正代表此次*Request*的实体，会明确给出请求的类型**RequestType**和请求的内容**RequestContent**。

#### 2.2 Session定义

*Session* 向所请求的*CloudApp*表明了会话的信息，每一次对*CloudApp*的请求都会产生会话信息，会话的信息和状态由开放平台的系统更新。*Session*也提供了*attributes*字段留给*CloudApp*来保存一些上下文信息。具体阐述如下：

```
"session": {
    "sessionId": "D75D1C9BECE045E9AC4A87DA86303DD6", 
    "newSession": true, 
    "attributes": {}
}
```

| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| sessionId              | string          | *每次会话的唯一ID，由系统填充*  |
| newSession  | boolean         | *true / false (由系统填充)*   |
| attributes             | key-value map          | *一个string-string map*         |

* **sessionId** - 每次会话的唯一ID，由系统填充
* **newSession** - 向CloudApp表明此次会话是新的会话还是已经存在的会话
* **attributes** - 为*CloudApp*提供*attributes*字段留保存上下文信息的字段

#### 2.3 Context定义

*Context* 向所请求的CloudApp提供了当前的设备信息，用户信息和应用状态，用以帮助CloudApp更好的去管理逻辑，状态以及对应的返回结果。

```
"context": {
    "application": {},
    "device": {},
    "user": {}
}
```

| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| application              | ApplicationInfo object          | *ApplicationInfo对象，目前只有应用ID*  |
| device  | DeviceInfo object         | *DeviceInfo对象*   |
| user          | UserInfo object          | *UserInfo对象*  |

##### 2.3.1 ApplicationInfo

*ApplicationInfo* 包含了当前的应用信息，目前只有**applicationId**可用。

```
"application": {
    "applicationId": "application id for requested CloudApp"
}
```

| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| applicationId     | string         | *应用ID字符串*  |

* **applicationId** - *CloudApp*在*Rokid开放平台*上的唯一ID.

##### 2.3.2 DeviceInfo

*DeviceInfo* 是对此次请求发生时当前设备信息的描述。

```
"device": {
    "basic":{},
    "screen":{},
    "media": {},
    "voice": {},
    "location": {}
}
```

| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| basic    | BasicInfo object          | *BasicInfo对象*  |
| screen    | ScreenInfo object          | *ScreenInfo对象*  |
| media     | MeidaStatus object          | *当前设备上CloudAppClient的MediaPlayer状态*  |
| location  | LocationInfo object          | *当前设备的地理位置信息*  |

* **basic** - 展示了当前设备的基础信息，主要包含设备制造信息、时间信息、国家文字信息。
* **screen** - 展示了当前设备的屏幕信息，主要包含屏幕的分辨率信息。
* **meida** - 向CloudApp表明当前设备上CloudAppClient中的MediaPlayer的状态信息。
* **location** - 向CloudApp提供当前设备的地理位置信息。

###### 2.3.2.1 BasicInfo

```
"basic":{
    "vendor":"vendor id",
    "deviceType":"device type",
    "deviceId": "010116000100",
    "locale": "zh-cn",
    "timestamp": 1478009510909
}
```
| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| vendor  | string         | *注册生产商ID*  |
| deviceType    | string         | *该生产商设定的设备型号*  |
| deviceId    | string         | *该型号下的设备ID*  |
| locale    | string         | *国家及语言，标准locale格式*  |
| timestamp    | long         | *当前时间，unix timestamp*  |


* **vendor** - 生产商ID，通过在网站注册生产商生成，保证全局唯一
* **deviceType** - 设备型号ID，通过在网站注册设备型号生成，保证生产商内部唯一
* **deviceId** - 设备ID，由生产商自行生成，保证设备型号内部唯一
* **locale** - 国家及语言，采用标准locale格式，language-country
* **timestamp** - 当前时间，使用设备当前的时间戳，unix timestamp

###### 2.3.2.2 ScreenInfo

当前设备的显示设备信息：

```
"screen":{
    "x":"640",
    "y":"480"
}
```

| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| x  | string         | *X 方向上的像素大小*  |
| y    | string         | *Y 方向上的像素大小*   |


* **x** - X 方向上的像素大小
* **y** - Y 方向上的像素大小
* 根据给出的屏幕分辨率信息，通常来讲，如果 **x** 比 **y** 大，那么该屏幕会被认为是横屏 **landscape**，反过来则是竖屏 **protrait**.

###### 2.3.2.3 MediaStatus

当前设备上CloudAppClient中MediaPlayer的状态：

```
"media": {
    "state": "PLAYING / PAUSED / IDLE"
}
```

| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| state  | string         | *PLAYING / PAUSED / IDLE*  |


* **state** - 表明当前播放状态. 当前**有 PLAYING** 、**PAUSED** 和 **IDLE** 三种状态可用。
    * **PLAYING**：代表当前有媒体正在播放；
    * **PAUSED**：代表当前媒体被暂停，可以执行继续播放（RESUME）操作；
    * **IDLE**：代表当前媒体播放器为空闲状态，没有任何媒体数据。


###### 2.3.2.4 VoiceStatus
参见上述[2.3.2.3 MediaStatus](#2323-mediastatus)

###### 2.3.2.5 LocationInfo

```
"location": {
    "latitude": "30.213322455923485",
    "longitude": "120.01190010997654"
}
```

当当前设备存在地理位置信息时会通过*location*提供给CloudApp。基于地理位置的CloudApp可以根据此信息来处理逻辑。*location* 信息目前包括 ***纬度(latitude)*** 和 ***经度(longtitude)***。

##### 2.3.3 UserInfo

UserInfo 展示了与当前设备绑定的用户信息，通常是设备对应手机应用的账号。

```
"user": {
    "userId": "user id string"
}
```

| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| userId  | string         | *用户ID*  |


#### 2.4 Request定义

*Request* 是当前请求的真正内容：

```
"request": {
    "reqType": "INTENT / EVENT",
    "reqId": "010116000100-ad1f462f4f0946ccb24e9248362c504a",
    "content": {}
}
```

| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| reqType  | string         | *INTENT / EVENT*  |
| reqId    | string         | *当前请求的唯一ID*  |
| content  | request content object         | *IntentRequest 或 EventRequest的对象*  |

* **reqType** - 表明请求的类型： **INTENT** 和 **EVENT** 分别对应 *IntentRequest* 和 *EventRequest*。
* **reqId** - 每次请求都会对应一个唯一ID用以区分每一次的请求。请求ID将会与返回ID一一对应。
* **content** - **IntentRequest** 和 **EventRequest** 对应的具体内容，下面会具体介绍。

##### 2.4.1 IntentRequest

IntentRequest 是基于 *NLP* 的结果产生的请求，其中包括了 *NLP* 的所有信息：**ApplicationId**， **Intent** 和 **Slots**。IntentRequest将会发给对应的 *CloudApp* 根据 *intent* 和 *slots* 进行相应的逻辑处理。

```
"content": {
    "applicationId": "com.rokid.cloud.music",
    "intent": "play_random",
    "slots": {}
}
```

| 字段               | 类型            | 可能值 |
|:-------:       |:--------------:|:-------------------------------|
| applicationId  | string         | *CloudApp 对应的 applicationId*  |
| intent         | string         | *CloudApp 对应的 nlp intent*     |
| slots          | string         | *CloudApp 对应的 nlp slots*      |

* **applicationId**, **intent** 和 **slots** 均为 **NLP** 结果的基本元素。分别表明了一句话所代表的领域，意图和完成这个意图所需要的参数。

##### 2.4.2 EventRequest

当CloudAppClient在执行中发生了一个事件，则会产生一个EventRequest。*CloudApp* 可以根据自己的需要选择处理或者不处理当前收到的事件。

```
"content": {
    "event": "Media.NEAR_FINISH",
    "extra": {
    	"key1": "value1",
    	"key2": "value2"
    }
}
```

| 字段               | 类型            | 可能值 |
|:-------:|:--------------:|:-------------------------------|
| event  | string         | *事件类型*   |
| extra  | string-string map         | *自定义字段，目前暂无定义，作扩展用*   |

* **event** - 表明了是具体的事件类型.
	* **Voice.STARTED** - 当Voice开始播放时发生
	* **Voice.FINISHED** - 当Voice停止是发生，此处停止可能是被打断，可能是播放完成，也可能是播放失败，但都作为统一的事件抛出。
	* **Media.START_PLAYING** - 当MediaPlayer开始播放时发生。
	* **Media.PAUSED** - 当MediaPlayer中途停止时发生。
	* **Media.NEAR_FINISH** - 当播放内容结束前15秒时发生，当总时长不足15秒时，会在 *Media.START_PLAYING** 后发生。
	* **Media.FINISHED** - 当播放内容结束时发生。
	* *更多的事件会在未来的版本更迭中给出*

* **extra** - 预留扩展字段，暂无定义。

### 3. Response

根据之前的描述，Response是 *CloudApp* 向客户端的返回结果。

#### 3.1 协议概览

整体协议示例如下：

```
{
    "version": "2.0.0",
    "session": {
        "attributes": {
    	    "key1": "value1",
    	    "key2": "value2"
        }
    },
    "response": {
        "action": { // for Rokid device

            "version":"2.0.0",

	    "type": "NORMAL / EXIT", 
	    
	    "form": "scene/cut/service",
            
            "shouldEndSession": true, 
            
            "voice": {
                "action": "PLAY/PAUSE/RESUME/STOP",
                "item": {
                    "tts": "tts content"
                }
            },

            "media": {
                "action": "PLAY/PAUSE/RESUME/STOP",
                "item": {
                    "token": "xxxx",
                    "type": "AUDIO/VIDEO",
                    "url": "media streaming url",
                    "offsetInMilliseconds": 0
                }
            },
	    
	    "display": {
	    	// TBD
	    },

	    "confirm": {
		"confirmIntent": "nlp intent to confirm",
		"confirmSlot": "nlp slot to confirm",
		"optionWords": ["word1","word2"],
	    }
        }
    }
}
```

* **version** - 表明了Response协议的版本，必须由 *CloudApp* 填充。当前协议版本是 `2.0.0`.
* **session** - 表示当前应用的session，与Request中的信息一致，*CloudApp* 可以在 *attributes* 里填充自己需要的上下文信息用于后面的请求。
* **response** - 返回给 *CloudAppClient* 的Response内容。包括了 `card` 和 `action` 两个部分。`card` 会在之后的协议更新中作详细说明。

#### 3.2 Action定义

Action 目前包括两种类型：`voice` 和 `media`。`voice` 表示了语音交互的返回。`media` 是对媒体播放的返回。

```
"action": {
    "version": "2.0.0",
    "type": "NORMAL/EXIT", 
    "form": "scene/cut/service",
    "shouldEndSession": true, 
    "voice": {},
    "media": {},
    "display": {},
    "confirm": {}
}
```


| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| version           | string          | *action 协议的版本，当前为 2.0.0*  |
| type              | string          | *NORMAL / EXIT*  |
| form              | string          | *scene / cut / service*  |
| shouldEndSession  | boolean         | *true / false*   |
| voice       | voice object    | *Voice对象*         |
| media             | media object          | *Media对象*         |

* **version** - 表明 action 协议版本，当前版本为: 2.0.0.
* **type** - 当前action的类型：`NORMAL` 或 `EXIT`。 当 `type` 是 `NORMAL` 时，`voice` 和 `media` 会同时执行；当 `type` 是 `EXIT` 时，action会立即退出，并且在这种情况下，`voice` 和 `media` 将会被会被忽略。
* **form** - 当前action的展现形式：scene、cut、service。scene的action会在被打断后压栈，cut的action会在被打断后直接结束，service会在后台执行，但没有任何界面。该字段在技能创建时被确定，无法由cloud app更改。
* **shouldEndSession** - 表明当此次返回的action执行完后 *CloudAppClient* 是否要退出，同时，当 `shouldEndSession` 为 `true` 时，*CloudAppClient* 将会忽略 *EventRequests*，即在action执行过程中不会产生 *EventRequest*。

##### 3.2.1 Voice

*Voice* 定义了 *CloudApp* 返回的语音交互内容。具体定义如下：

```
"voice": {
    "action":"PLAY/PAUSE/RESUME/STOP",
    "item": {}
}
```

| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| action              | string          | *PLAY / PAUSE / RESUME / STOP*  |
| item       | item object    | *voice 的 item 对象*         |

* **action** - 表示对当前voice的操作，可以播放（PLAY)、暂停（PAUSE）、继续播放（RESUME）和停止（STOP）（具体Action行为参照Media的Action行为，但是目前暂未实现，**PAUSE**以及**RESUME**操作）;
* **item** - 定义了voice的具体内容，将会在 *3.2.1.1* 中详细描述.

###### 3.2.1.1 Item

Item定义了voice的具体内容。

```
"item": {
    "tts": "tts content"
}
```

| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| tts              | string          | *tts 内容*  |

* **tts** - 定义了需要播报的TTS内容。

##### 3.2.2 Media

Media 用来播放CloudApp返回的流媒体内容。有 *audio* 和 *video* 两种类型，目前第一版暂时只对 *audio* 作了支持，后续会支持 *video*。

```
"media": {
    "action": "PLAY/PAUSE/RESUME/STOP",
    "item": {}
}
```

| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| action              | string          | *PLAY / PAUSE / RESUME / STOP*  |
| item  | media item object        | *media的具体内容*  |

* **action** - 定义了对MediaPlayer的操作，目前只支持 **4** 种操作：**PLAY**， **PAUSE** ， **RESUME** 和 **STOP**。其中，只有**PLAY**接受**item**数据。
    * **PLAY**：如果有**item**数据，则按照最新的**item**从头开始播放，如果没有**item**数据，且原来有在播放的内容，则从原来播放的内容开始播放
    * **PAUSE**：暂停当前播放的内容，播放的进度等数据不会丢失（可以直接通过**RESUME**指令直接恢复原来的播放状态）
    * **RESUME**：继续播放（从原来的播放进度播放）
    * **STOP**：停止播放，并且清空当前的播放进度，但是播放内容不清
* **item** - 定义了具体的播放内容，如下：

###### 3.2.2.1 Item

```
"item": {
    "token": "xxxx",
    "type": "AUDIO/VIDEO",
    "url": "media streaming url",
    "offsetInMilliseconds": 0
}
```

| 字段               | 类型            | 可能值 |
|:-----------------:|:---------------:|:---------------|
| token              | string          | *用于鉴权的token，由CloudApp填充和判断*  |
| type          | string          | *AUDIO / VIDEO* |
| url  | string        | *可用的流媒体播放链接*  |
| offsetInMilliseconds  | long        | *毫秒数值，表明从哪里开始播放*  |

* **token** - 用于鉴权的token，由CloudApp填充和判断。
* **type** - 表明了当前媒体类型：**AUDIO** 或 **VIDEO**，有且只能取其一。
* **url** - 为MediaPlayer指明可用的流媒体播放链接。
* **offsetInMilliseconds** - 毫秒数值，告诉MediaPlayer开始播放的位置。有效范围从0到歌曲整体播放时长。

###### Copyright © 2017 Rokid Corporation Ltd. All rights reserved.




