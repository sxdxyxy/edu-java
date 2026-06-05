<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>学习记录</title>
    <style>
        .container {
            user-select: none;
            -webkit-user-drag: none;
            transform-origin: left top;
            width: 650px;
            height: 903px;
            margin: 0 auto;
            box-sizing: border-box;
            position: relative;
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
        .tab {
            width: 100%;
            height: 100%;
            border: 2px solid gray;
            border-collapse: collapse;
        }
        .tab td {
            border: 1px solid gray;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="content">
        <table class="tab">
            <caption>${record.title}</caption>
            <tbody>
            <tr>
                <td>姓名</td>
                <td>${ record.personName }</td>
                <td>性别</td>
                <td>${ record.sex }</td>
                <td>出生日期</td>
                <td>${record.birthday}</td>
            </tr>
            <tr>
                <td>身份证</td>
                <td>${ record.idCardNo }</td>
                <td>联系方式</td>
                <td>${record.phone}</td>
                <td>学习时间</td>
                <td>${ record.startDate}至${record.endDate}</td>
            </tr>
            <tr>
                <td>所在部门</td>
                <td colspan="5"></td>
            </tr>
            <tr>
                <td>岗位或工种</td>
                <td colspan="5">${record.workTypeName}</td>
            </tr>
            <tr>
                <td>课程内容</td>
                <td colspan="5">
                    <#list record.courseList as item>
                        ${item.courseName}、
                    </#list>
                </td>
            </tr>
            <tr>
                <td>受教育情况</td>
                <td colspan="5">成绩合格，予以毕业。</td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
<script>
    <#noparse >
    function setScale() {
        document.getElementsByClassName("container")[0].style.transform = `scale(${window.screen.width / 660})`;
    }
    setScale()
    </#noparse>
</script>
</body>
</html>
