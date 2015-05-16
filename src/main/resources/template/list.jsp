<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>网站后台管理</title>
<link rel="stylesheet" href="<c:url value="/resources_back/css/common/reset.css"/>" type="text/css" media="screen" />
<link rel="stylesheet" href="<c:url value="/resources_back/css/common/style.css"/>" type="text/css" media="screen" />
<link rel="stylesheet" href="<c:url value="/resources_back/css/common/invalid.css"/>" type="text/css" media="screen" />
<link rel="shortcut icon" href="resources/images/favicon.ico" />
<script type="text/javascript" src="<c:url value="/resources_back/js/common/jquery-1.3.2.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources_back/js/common/simpla.jquery.configuration.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources_back/js/common/facebox.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources_back/js/common/jquery.wysiwyg.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources_back/js/common/jquery.datePicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources_back/js/common/jquery.date.js"/>"></script>

</head>
<body>
<div id="body-wrapper">

  <%@include file="/common_back/left.jsp"%>
  <!-- End #sidebar -->
  <div id="main-content">
    <%@include file="/common_back/header.jsp"%>
    <div class="content-box">
      <!-- Start Content Box -->
      <div class="content-box-header">
        <h3>兴趣标签维护</h3>
        <div class="clear"></div>
      </div>
      <!-- End .content-box-header -->
      <div class="content-box-content">
        <div class="tab-content default-tab" id="tab1">
          <!-- This is the target div. id must match the href of this div's tab -->
          <table>
            <thead>
              <tr>
                <th>
                  <input class="check-all" type="checkbox" />
                </th>
                <th>分属分类</th>
                <th>标签名称</th>
                <th>标签类型</th>
                <th>使用次数</th>
                <th>创建时间</th>
                <th>创建人ID</th>
              </tr>
            </thead>
            <tfoot>
              <tr>
                <td colspan="8">
                  <div class="bulk-actions align-left">
                    <select name="dropdown">
                      <option value="option1">Choose an action...</option>
                      <option value="option2">Edit</option>
                      <option value="option3">Delete</option>
                    </select>
                    <a class="button" href="#">Apply to selected</a> </div>
                  <div class="pagination">${pageHtml}</div>
                  <!-- End .pagination -->
                  <div class="clear"></div>
                </td>
              </tr>
            </tfoot>
            <tbody>
                <c:forEach items="${quTags}" var="quTag">
	              <tr>
	                <td>
	                  <input type="checkbox" />
	                </td>
	                <td>${quTag.quTypeId}</td>
	                <td>${quTag.quTagName}</td>
	                <td>${quTag.quTagFlag}</td>
	                <td>${quTag.useCount}</td>
	                <td><fmt:formatDate value="${quTag.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	                <td>${quTag.userId}</td>
	                <td>
	                  <!-- Icons -->
	                  <a href="quTypeUpdateInit" title="Edit"><img src="resources_back/images/common/icons/pencil.png" alt="Edit" /></a>
	                  <a href="javascript:void(0);" title="Delete"><img src="resources_back/images/common/icons/cross.png" alt="Delete" /></a>
	                  <a href="javascript:void(0);" title="Edit Meta"><img src="resources_back/images/common/icons/hammer_screwdriver.png" alt="Edit Meta" /></a>
	                </td>
	              </tr>
                </c:forEach>
            </tbody>
          </table>
        </div>
        <!-- End #tab1 -->
      </div>
      <!-- End .content-box-content -->
    </div>
    <!-- End .content-box -->
    <div class="clear"></div>

    <%@include file="/common_back/footer.jsp"%>

    <!-- End #footer -->
  </div>
  <!-- End #main-content -->
</div>
</body>
<!-- Download From www.exet.tk-->
</html>
