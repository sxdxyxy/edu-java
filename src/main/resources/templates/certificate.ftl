<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>证书</title>
    <style>
        .certificate-container {
            user-select: none;
            -webkit-user-drag: none;
            transform-origin: left top;
            width: 650px;
            height: 903px;
            margin: 0 auto;
            background: url("https://static.joyfishs.com/images/certificate-bg2.jpg") no-repeat;
            background-size: 100% 100%;
            box-sizing: border-box;
            position: relative;
        }
        .certificate-container img {
            user-select: none;
            -webkit-user-drag: none;
        }

        .certificate-no {
            padding-top: 200px;
            text-align: center;
            font-family: SimSun;
            font-weight: 400;
            font-size: 15px;
            color: #2c2d2d;
            line-height: 49px;
        }

        .certificate-photo {
            width: 135px;
            height: 177px;
            border: 1px solid #353f47;
            margin: 24px auto 0;
        }
        .certificate-photo img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .content {
            margin-top: 36px;
            text-indent: 30px;
            line-height: 1.7;
            letter-spacing: 2px;
            font-family: SimSun;
            font-size: 22px;
            color: #323333;
            padding: 0 50px;
        }

        .footer {
            margin-top: 96px;
            padding-left: 58px;
            display: flex;
            align-items: center;
        }

        .footer .qrcode-wrap {
            width: 128px;
            height: 128px;
            border: 1px solid #4286c7;
        }

        .footer .qrcode-wrap img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .company-info-wrap {
            margin-left: 60px;
            font-family: SimSun;
            font-weight: 400;
            font-size: 15px;
            color: #323333;
            line-height: 1.7;
        }
    </style>
</head>
<body>
<div class="certificate-container">
    <div class="certificate-wrap">
        <div class="certificate-no">证书编号：A${certificate.number}</div>
        <div class="certificate-photo">
            <img src="${certificate.facePhotoUrl}" alt="人脸照片" />
        </div>
        <div class="content">
            ${ certificate.personName }(${ certificate.idCardNo })${ certificate.sex }，于${ certificate.startDate }参加${ certificate.title }培训，学时修满，考核成绩合格。
        </div>
        <div class="footer">
            <div class="qrcode-wrap">
                <img src="https://sts.joyfishs.com/api/person/getStudyRecordQRCode?personId=${ personId?c }&projectId=${ projectId?c }" class="qrcode-img" />
            </div>

            <div class="company-info-wrap">
                <div>培训考核企业:三峡绿色发展有限公司</div>
                <div>适用范围:三峡绿色发展有限公司</div>
            </div>
        </div>
    </div>
</div>

<script>
    <#noparse >
    function setScale() {
        document.getElementsByClassName("certificate-container")[0].style.transform = `scale(${window.screen.width / 660})`;
    }
    function resetFont(){
        if (typeof(WeixinJSBridge) == "undefined") {
            document.addEventListener("WeixinJSBridgeReady", function (e) {
                setTimeout(function(){
                    WeixinJSBridge.invoke('setFontSizeCallback',{"fontSize":0}, function(res) {
                        // alert(JSON.stringify(res));
                    });
                },0);
            });
        } else {
            setTimeout(function(){
                WeixinJSBridge.invoke('setFontSizeCallback',{"fontSize":0}, function(res) {
                    // alert(JSON.stringify(res));
                });
            },0);
        }
    }
    setScale()
    resetFont()
    </#noparse>
</script>
</body>
</html>
