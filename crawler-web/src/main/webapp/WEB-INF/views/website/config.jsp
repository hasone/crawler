<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ include file="../include/include.jsp"%>
<%@ page isELIgnored="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>舆情网络爬虫</title>
<link href="<c:url value="/resources/form.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/index.css" />" rel="stylesheet" type="text/css" />
<script type="text/javascript">
	$(function() {
		$('fieldset > legend').next('table').hide();
		$('fieldset > legend').click(function() {
			if ($(this).next('table').is(':hidden')) {
				$(this).next('table').show();
			} else {
				$(this).next('table').hide();
			}
		});
		$('#addSectionBtn').click(function() {
			$('div.form-wrapper').show();
		});
		$('a.form-wrapper-close').click(function() {
			$('div.form-wrapper').hide();
			$('#message').hide();
		});
		
		/* 列表页 */
		$('#url').blur(function() {
			var url = $('#url').val();
			if (url != '') {
				$.get('website/listConfExist',{url: url}, function(data) {
					console.log(data);
					if (data == true) {
						$('#confdetail' + index).find('label[for=url]').css('border', '1px solid #ff0000');
						$('#confdetail' + index).find('label[for=url]').attr('title', '已存在');
					} else {
						$('#urlerror').text('');
					}
				});
			}
		});
		
		$("#testConfList").click(function() {
			$('#loading').show();
			$.post('config/testListConf', $('#conflist').serialize(), function(data) {
				$('#loading').hide();
				data = $.parseJSON(data);
				if (data.errors != undefined) {
					console.log (data.errors);
					$.each(data.errors, function(key, val) {
						$('#' + key).text(val);
					});
				}
				var html = '<strong>配置结果:</strong><p>分页栏:' + data.pagebar + '</p>';
				html += '<table><tr><td>编号</td><td>详细页</td><td>更新时间</td></tr>';
				$.each(data.list, function(i, item){
					html += '<tr>';
					html += '<td class="link">' + (i+1) + '</td>';
					html += '<td><a class="link" target="_blank" href="' + item.url + '">' + item.title + '</a></td>';
					html += '<td>' + (item.update == null ? '' : new Date(item.update).toLocaleString()) + '</td>';
					html += '</tr>';
				});
				html += '</table>';
				$("#message div.form-wrapper-center").html(html);
				$('#message').show();  
			});
		});
		
		$("#saveConfList").click(function() {
			$('#loading').show();
			$('#conflist').form('submit', {
				url: 'config/addListConf',
				onSubmit:function(param) {
					console.log('validate');
					return $(this).form('enableValidation').form('validate');
				},
				success : function(data) {
					$('#loading').hide();
					data = $.parseJSON(data);
					if (data.errors != undefined) {
						console.log (data.errors);
						$.each(data.errors, function(key, val) {
							$('#' + key).text(val);
						});
					}
					
					if (data.msg == 'success') {
						$.messager.show({
			                title:'保存结果',
			                msg:'保存成功',
			                timeout:5000,
			                showType:'show',
			                style:{
			                    right:'',
			                    top:document.body.scrollTop+document.documentElement.scrollTop,
			                    bottom:''
			                }
			            });
					}
				}
			});
		});
		
		/* 详细页配置 */
		/* $("input[id^='host']").blur(function() {
			$.get('websiteInfo/detailConfExist', function(data) {
				console.log(data);
				if (date == true) {
					$('#hosterror').text('Host已存在');
				} else {
					$('#hosterror').text('');
				}
			});
		}); */
		
		$("a[id^='testConfDetail']").click(function(e) {
			e.preventDefault();
			var index = $(this).attr('id').split('testConfDetail')[1];
			
			$('label.error').text('');
			$('#saveMessage').html('');
			
			$('#confdetail' + index).form('submit', {
				url: 'config/testDetailConf',
				onSubmit:function(param) {
					return $(this).form('enableValidation').form('validate');
				},
				success : function(data) {
					data = $.parseJSON(data);
					$('#confdetail' + index).find("label[id$='error']").text('');
					if (data.errors != undefined && data.errors.length != 0) {
						$.each(data.errors, function(i, val) {
							$('#confdetail' + index).find('label[for=' + val.field + ']').css('border', '1px solid #ff0000');
							$('#confdetail' + index).find('label[for=' + val.field + ']').attr('title', val.msg);
						});
					}
					var html = '<div class="conftable">';
					html += '<div>';
					if (data.info != undefined){
						$.each(data.info, function(i, val) {
							html += '<p><strong>' + i + ':</strong>' + val + '</p>';
						});
					}
					html += '</div></div>';
					
					$("#message div.form-wrapper-center").html(html);
					$('#message').show();  
				}
			});
		}); 
		$("a[id^='saveConfDetail']").click(function(e){
			e.preventDefault();
			var index = $(this).attr('id').split('saveConfDetail')[1];
			
			$('label.error').text('');
			$('#saveMessage').html('');
			$('#confdetail' + index).form('submit', {
				url: 'config/saveDetailConf',
				onSubmit:function(param) {
					return $(this).form('enableValidation').form('validate');
				},
				success : function(data) {
					data = $.parseJSON(data);
					$('#confdetail' + index).find('label').css('border', 'none');
					$('#confdetail' + index).find('label').attr('title', '');
					
					if (data.errors != undefined && data.errors.length != 0) {
						$.each(data.errors, function(i, val) {
							$('#confdetail' + index).find('label[for=' + val.field + ']').css('border', '1px solid #ff0000');
							$('#confdetail' + index).find('label[for=' + val.field + ']').attr('title', val.msg);
						});
					}
					if (data.msg == 'success') {
						$.messager.show({
			                title:'保存结果',
			                msg:'保存成功',
			                timeout:4000,
			                showType:'show',
			                style:{
			                    right:'',
			                    top:document.body.scrollTop+document.documentElement.scrollTop,
			                    bottom:''
			                }
			            });
					} else {
						$.messager.show({
			                title:'保存失败',
			                msg:'配置有误,保存失败',
			                timeout:4000,
			                showType:'show',
			                style:{
			                    right:'',
			                    top:document.body.scrollTop+document.documentElement.scrollTop,
			                    bottom:''
			                }
			            });
					}
				}
			});
		});
	});
</script>
</head>
<body>
	<jsp:include page="../include/header.jsp"></jsp:include>
	<div id="body">
		<div>
			<a class="linkbutton" href="javascript:history.go(-1);">返回</a>
			<h2>${section.comment }</h2>
			<c:if test="${!empty confList }">
				<a id="addSectionBtn" class="linkbutton" href="javascript:void(0);">以此规则添加版块</a>
			</c:if>
		</div>
		<div id="message">
			<a class="form-wrapper-close" href="javascript:;"></a>
			<div class="form-wrapper-title">验证结果</div>
			<div class="form-wrapper-center" style="height: 300px; overflow-y: scroll;" >
			</div>
		</div>
		<div style="text-align:center;">
			<div id="loading" ></div>
		</div>
		<div class="form-wrapper" style="display: none;">
			<a class="form-wrapper-close" href="javascript:;"></a>
			<div class="form-wrapper-title">以此规则添加新版块</div>
			<div class="form-wrapper-center">
				<form id="sectionForm" method="post" data-options="novalidate:true">
					<div>
						<label class="form-label" class="form-label" for="name">版块地址</label> <input class="easyui-validatebox form-input"
							type="text" name="url" data-options="required:true" />
					</div>
					<div>
						<label class="form-label" class="form-label" for="email">版块名称</label> <input class="easyui-validatebox form-input"
							type="text" name="comment" data-options="required:true" />
					</div>
					<div>
						<input class="form-btn" type="submit" value="保存" />
					</div>
				</form>
			</div>
		</div>
		<div id="content">
			<!-- 列表页配置, 一个版块只有一个列表页配置 -->
			<div class="conf-panel">
				<div class="easyui-panel" title="列表页配置">
					<div style="padding: 10px 60px 20px 60px">
						<div>
							<form class="confform" id="conflist" method="post">
								<div class="confdiv" style="float: left; text-align: right;">
									<div>
										 <input type="hidden" name="comment" id="comment" value="${section.comment }"  />
									 	<input type="hidden" name="url" id="url" value="${section.url }" />
									</div>
									<div>
										<label class="form-label" for="auth">是否需要登录</label>
										<c:choose>
											<c:when test="${confList.auth}">
												<input type="radio" name="auth" value="true" checked="checked" />是<input type="radio" name="auth"
													value="false" /> 否</c:when>
											<c:otherwise>
												<input type="radio" name="auth" value="true" />是<input type="radio" name="auth" value="false"
													checked="checked" /> 否
											</c:otherwise>
										</c:choose>
									</div>
									<div>
										<label class="form-label" for="ajax">是否Ajax加载</label>
										<c:choose>
											<c:when test="${confList.ajax }">
												<input type="radio" name="ajax" value="true" checked="checked" />是<input type="radio" name="ajax"
													value="false" /> 否</c:when>
											<c:otherwise>
												<input type="radio" name="ajax" value="true" />是<input type="radio" name="ajax" value="false"
													checked="checked" /> 否</c:otherwise>
										</c:choose>
									</div>
									<div>
										<label class="form-label" for="numThreads">线程数<span class="red">*</span></label> <input class="easyui-numberbox "
											name="numThreads" data-options="required:true,min:1,precision:0,value:6" value="${confList.numThreads }" />
									</div>
									<div>
										<label class="form-label" for="fetchinterval">时间间隔(分钟)<span class="red">*</span></label> <input name="fetchinterval"
											class="easyui-numberbox" data-options="required:true,min:1,precision:0,value:30" value="${confList.fetchinterval }" />
									</div>
									<div>
										<label class="form-label" for="filterurl">过滤URL的正则表达式</label> <input name="filterurl" class="easyui-textbox "
											value="${confList.filterurl }" />
									</div>
								</div>
								<div class="confdiv" style="float: right; text-align: right;">
									<div>
										<label class="form-label" for="listdom">列表页DOM<span class="red">*</span></label> <input name="listdom"
											class="easyui-textbox" data-options="required:true" value="${confList.listdom }" /> 
									</div>
									<div>
										<label class="form-label" for="linedom">列表行DOM<span class="red">*</span></label> <input name="linedom"
											class="easyui-textbox" data-options="required:true" value="${confList.linedom }" /> 
									</div>
									<div>
										<label class="form-label" for="urldom">详细页URL DOM<span class="red">*</span></label> <input name="urldom"
											class="easyui-textbox" data-options="required:true" value="${confList.urldom }" /> 
									</div>
									<div>
										<label class="form-label" for="updatedom">更新时间DOM</label> <input name="updatedom" class="easyui-textbox"
											value="${confList.updatedom}" /> <label class="form-label" class="error" id="updatedomerror"></label>
									</div>
									<div>
										<label class="form-label" for="datedom">发布日期DOM</label> <input name="datedom" class="easyui-textbox"
											value="${confList.datedom }" /> 
									</div>
									<div>
										<label class="form-label" title="搜索时的简介DOM" for="synopsisdom">简介DOM</label> <input name="synopsisdom" class="easyui-textbox"
											value="${confList.synopsisdom }" />
									</div>
								</div>
							</form>
						</div>
						<div style="text-align: center; padding: 5px;">
							<a href="javascript:void(0)" class="easyui-linkbutton" id="testConfList">验证</a> <a
								href="javascript:void(0)" class="easyui-linkbutton" id="saveConfList">保存</a>
						</div>
					</div>
				</div>
			</div>
			<!-- 详细配置, 一个版块可能包含多个详细页配置 -->
			<c:choose>
				<c:when test="${empty confDetails}">
					<div class="conf-panel">
						<div class="easyui-panel conf-panel" title="详细页配置">
							<div style="padding: 10px 60px 20px 60px">

								<form id="confdetail" method="post">
									<div class="confdiv" style="float: left; text-align: right;">
										<div><input name="id.listurl" type="hidden" value="${confList.url }" /></div>
										<div>
											<label class="form-label" for="testUrl">测试页URL地址<span class="red">*</span>
											</label> <input name="testUrl" class="easyui-textbox" data-options="required:true, validType:'url'" /> <label class="form-label"
												class="error" id="testUrlerror"></label>
										</div>
										<div>
											<label class="form-label" for="host">域名<span class="red">*</span>
											</label> <input name="host" class="easyui-textbox " style="height: 60px;" data-options="required:true, multiline:true" />
										</div>
										<div>
											<label class="form-label" for="replyNum">回复数DOM</label>
											<input name="replyNum" class="easyui-textbox easyui-tooltip" />
										</div>
										<div>
											<label class="form-label" for="reviewNum">浏览数DOM</label> <input name="reviewNum" class="easyui-textbox" /> 
										</div>
										<div>
											<label class="form-label" for="forwardNum">转发数DOM</label> <input name="forwardNum" class="easyui-textbox" /> 
										</div>
										<div>
											<label class="form-label" for="sources">来源DOM </label> <input name="sources" class="easyui-textbox" /> 
										</div>
										<div>
											<label class="form-label" for="fetchorder">抓取顺序</label> <input type="radio" name="fetchorder" value="false" /> 从第一页开始<input
												type="radio" name="fetchorder" value="true" checked="checked" />从最后一页开始
										</div>
										<fieldset style="border: 1px solid #e3e3de">
											<legend>主帖</legend>
											<div>
												<label class="form-label" for="master">主帖DOM<span class="red">*</span>
												</label> <input name="master" class="required, easyui-textbox" /> 
											</div>
											<div>
												<label class="form-label" for="author">楼主DOM<span class="red">*</span>
												</label> <input name="author" class="required, easyui-textbox" />
											</div>
											<div>
												<label class="form-label" for="date">发布时间DOM </label> <input name="date" class="easyui-textbox" />
											</div>
											<div>
												<label class="form-label" for="content">内容DOM<span class="red">*</span>
												</label> <input name="content" class="required, easyui-textbox" /> 
											</div>
										</fieldset>
									</div>
									<div class="confdiv" style="float: right; text-align: right;">
										<fieldset style="border: 1px solid #e3e3de">
											<legend>回复</legend>
											<div>
												<label class="form-label" for="reply">回复DOM </label> <input name="reply" class="easyui-textbox" />  
											</div>
											<div>
												<label class="form-label" for="replyAuthor">作者DOM </label> <input name="replyAuthor" class="easyui-textbox" />  
											</div>
											<div>
												<label class="form-label" for="replyDate">发布时间DOM</label> <input name="replyDate" class="easyui-textbox" />  
											</div>
											<div>
												<label class="form-label" for="replyContent">内容DOM </label> <input name="replyContent" class="easyui-textbox" />  
											</div>
										</fieldset>
										<fieldset style="border: 1px solid #e3e3de">
											<legend>子回复</legend>
											<div>
												<label class="form-label" for="subReply">子回复DOM</label> <input name="subReply" class="easyui-textbox" />  
											</div>
											<div>
												<label class="form-label" for="subReplyAuthor">作者DOM</label> <input name="subReplyAuthor" class="easyui-textbox" />  
											</div>
											<div>
												<label class="form-label" for="subReplyDate">发布时间DOM</label> <input name="subReplyDate" class="easyui-textbox" />  
											</div>
											<div>
												<label class="form-label" for="subReplyContent">内容DOM</label> <input name="subReplyContent" class="easyui-textbox" />  
											</div>
										</fieldset>
									</div>
								</form>
							</div>
							<div style="text-align: center; padding: 5px">
								<a href="javascript:void(0)" class="easyui-linkbutton" onclick="validateConfDetail()">验证</a> <a
									href="javascript:void(0)" class="easyui-linkbutton" onclick="saveConfDetail()">保存</a>
							</div>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<c:forEach items="${confDetails}" var="confDetail" varStatus="status">
						<div class="conf-panel">
							<div class="easyui-panel conf-panel" title="详细页配置${status.count }">
								<div style="padding: 10px 60px 20px 60px">
									<form id="confdetail${status.count }" method="post">
										<div class="confdiv" style="float: left; text-align: right;">
											<div>
												<input name="id.listurl" type="hidden" value="${confList.url }" />
												<input name="oldHost" type="hidden" value="${confDetail.id.host }" />
											</div>
											<div>
												<label class="form-label" for="testUrl">测试页URL地址<span class="red">*</span></label><input name="testUrl"
													class="easyui-textbox" data-options="required:true, validType:'url'" />
											</div>
											<div>
												<label class="form-label" for="host">域名<span class="red">*</span></label><input name="id.host"
													value="${confDetail.id.host }" class="easyui-textbox" style="height: 60px;" data-options="required:true, multiline:true" />
											</div>
											<div>
												<label class="form-label" for="replyNum">回复数DOM</label>
												<input name="replyNum" value="${confDetail.replyNum }" class="easyui-textbox easyui-tooltip" /> <label class="form-label" class="error"
													id="replyNumerror"></label>
											</div>
											<div>
												<label class="form-label" for="reviewNum">浏览数DOM </label> <input name="reviewNum" value="${confDetail.reviewNum }"
													class="easyui-textbox" /> 
											</div>
											<div>
												<label class="form-label" for="forwardNum">转发数DOM </label> <input name="forwardNum" value="${confDetail.forwardNum }"
													class="easyui-textbox" /> 
											</div>
											<div>
												<label class="form-label" for="sources">来源DOM </label> <input name="sources" value="${confDetail.sources }"
													class="easyui-textbox" />  
											</div>
											<div>
												<label class="form-label" for="fetchorder">抓取顺序</label>
												<c:choose>
													<c:when test="${confDetail.fetchOrder }">
														<input type="radio" name="fetchorder" value="false" checked="checked" /> 从第一页开始
																	<input type="radio" name="fetchorder" value="true" />从最后一页开始
																</c:when>
													<c:otherwise>
														<input type="radio" name="fetchorder" value="false" /> 从第一页开始
																	<input type="radio" name="fetchorder" value="true" checked="checked" />从最后一页开始
																</c:otherwise>
												</c:choose>
											</div>
											<fieldset style="border: 1px solid #e3e3de">
												<legend>主帖</legend>
												<div>
													<label class="form-label" for="master">主帖DOM<span class="red">*</span></label> <input name="master"
														value="${confDetail.master }" class="easyui-textbox" data-options="required:true" />  
												</div>
												<div>
													<label class="form-label" for="author">作者DOM</label> <input name="author"
														value="${confDetail.author}" class="easyui-textbox"  /> <label class="form-label" class="error"
														id="authorerror"></label>
												</div>
												<div>
													<label class="form-label" for="date">发布时间DOM </label> <input name="date" value="${confDetail.date }"
														class="easyui-textbox" />
												</div>
												<div>
													<label class="form-label" for="content">内容DOM<span class="red">*</span></label> <input name="content"
														class="easyui-textbox" value="${confDetail.content }" data-options="required:true" />
												</div>
											</fieldset>
										</div>
										<div class="confdiv" style="float: right; text-align: right;">
											<fieldset style="border: 1px solid #e3e3de">
												<legend>回复</legend>
												<div>
													<label class="form-label" for="reply">回复DOM </label> <input name="reply" value="${confDetail.reply}"
														class="easyui-textbox" />
												</div>
												<div>
													<label class="form-label" for="replyAuthor">作者DOM </label> <input name="replyAuthor" value="${confDetail.replyAuthor}"
														class="easyui-textbox" /> 
												</div>
												<div>
													<label class="form-label" for="replyDate">发布时间DOM</label> <input name="replyDate" value="${confDetail.replyDate}"
														class="easyui-textbox" />
												</div>
												<div>
													<label class="form-label" for="replyContent">内容DOM </label> <input name="replyContent" value="${confDetail.replyContent}"
														class="easyui-textbox" /> 
												</div>
											</fieldset>
											<fieldset style="border: 1px solid #e3e3de">
												<legend>子回复</legend>
												<div>
													<label class="form-label" for="subReply">子回复DOM</label> <input name="subReply" value="${confDetail.subReply}"
														class="easyui-textbox" /> 
												</div>
												<div>
													<label class="form-label" for="subReplyAuthor">作者DOM</label> <input name="subReplyAuthor" value="${confDetail.subReplyAuthor}"
														class="easyui-textbox" />
												</div>
												<div>
													<label class="form-label" for="subReplyDate">发布时间DOM</label> <input name="subReplyDate" value="${confDetail.subReplyDate}"
														class="easyui-textbox" />
												</div>
												<div>
													<label class="form-label" for="subReplyContent">内容DOM</label> <input name="subReplyContent" value="${confDetail.subReplyContent}"
														class="easyui-textbox" /> 
												</div>
											</fieldset>
										</div>
									</form>
								</div>
								<div style="text-align: center; padding: 5px">
									<a href="javascript:void(0)" class="easyui-linkbutton" id="testConfDetail${status.count }">验证</a> <a
										href="javascript:void(0)" class="easyui-linkbutton" id="saveConfDetail${status.count }">保存</a>
								</div>
							</div>
						</div>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<jsp:include page="../include/footer.jsp"></jsp:include>
</body>
</html>