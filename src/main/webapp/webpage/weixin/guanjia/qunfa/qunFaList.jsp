<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<t:datagrid name="qunfalist" checkbox="true" actionUrl="qunFaController.do?datagrid" fit="true" fitColumns="true" idField="id" queryMode="group">
	<t:dgCol title="编号" field="id" hidden="false" ></t:dgCol>
	<t:dgCol title="文本名称" field="name" query="true" width="100"></t:dgCol>
	<t:dgCol title="文本内容" field="content" width="100"></t:dgCol>
	<t:dgCol title="类型" field="type" width="100"></t:dgCol>
	<t:dgCol title="操作" field="opt"></t:dgCol>
	<t:dgDelOpt title="删除" url="qunFaController.do?del&id={id}" />
	<t:dgConfOpt title="群发" url="qunFaController.do?send&id={id}"  message="确认群发" />
	<t:dgToolBar title="文本录入" icon="icon-add" url="qunFaController.do?addorUpdate" funname="add"></t:dgToolBar>
 	<t:dgToolBar title="文本编辑" icon="icon-edit" url="qunFaController.do?addorUpdate" funname="update"></t:dgToolBar>
 	<t:dgToolBar title="批量删除"  icon="icon-remove" url="qunFaController.do?doBatchDel" funname="deleteALLSelect" ></t:dgToolBar>
</t:datagrid>


