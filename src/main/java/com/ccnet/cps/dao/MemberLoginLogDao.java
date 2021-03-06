package com.ccnet.cps.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import cn.ffcs.memory.BeanListHandler;

import com.ccnet.core.common.utils.CPSUtil;
import com.ccnet.core.common.utils.dataconvert.Dto;
import com.ccnet.core.dao.base.BaseDao;
import com.ccnet.core.dao.base.Page;
import com.ccnet.cps.entity.MemberLoginLog;

@Repository("memberLoginLogDao")
public class MemberLoginLogDao extends BaseDao<MemberLoginLog> {
	
	
	/**
	 * 分页查询日志
	 * @param lg
	 * @param page
	 * @param queryParam
	 * @return
	 */
	public Page<MemberLoginLog> findMemberLoginLogByPage(MemberLoginLog lg, Page<MemberLoginLog> page,String queryParam) {
		
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sql.append("select * from ").append(getCurrentTableName());

		List<String> whereColumns = memory.parseWhereColumns(params, MemberLoginLog.class, lg);
		if (CollectionUtils.isNotEmpty(whereColumns)) {
			sql.append(" where ").append(appendAnd(whereColumns));
		}
		//加上模糊查询
		if(CPSUtil.isNotEmpty(queryParam)){
			if(sql.toString().indexOf("where")<0){
			   sql.append(" where 1=1 ");
			}
			sql.append(" and (login_ip like ?  or user_id like ?) ");
			params.add("%" + queryParam + "%");
			params.add("%" + queryParam + "%");
		}
		
		//加上排序
		sql.append(" order by login_time desc ");
		super.queryPager(sql.toString(), new BeanListHandler<MemberLoginLog>(MemberLoginLog.class), params, page);
		return page;
		
	}
	
	/**
	 * 分页查询日志(多过滤)
	 * @param lg
	 * @param page
	 * @param pdDto
	 * @return
	 */
	public Page<MemberLoginLog> findMemberLoginLogByPage(MemberLoginLog lg, Page<MemberLoginLog> page,Dto pdDto){
		
		//获取参数
		String queryParam = pdDto.getAsString("queryParam");
		//日期过滤
		String end_date = pdDto.getAsString("end_date");
		String start_date = pdDto.getAsString("start_date");
		
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sql.append("select * from ").append(getCurrentTableName());

		List<String> whereColumns = memory.parseWhereColumns(params, MemberLoginLog.class, lg);
		if (CollectionUtils.isNotEmpty(whereColumns)) {
			sql.append(" where ").append(appendAnd(whereColumns));
		}
		
		//加上模糊查询
		if(CPSUtil.isNotEmpty(queryParam)){
			appendWhere(sql);
			sql.append(" and (login_ip like ?  or user_id like ?) ");
			params.add("%" + queryParam + "%");
			params.add("%" + queryParam + "%");
		}
		
		//带上日期查询
		if(CPSUtil.isNotEmpty(start_date)){
			appendWhere(sql);
			sql.append(" and login_time >=? ");
			params.add(start_date+" 00:00:00");
		}
		
        if(CPSUtil.isNotEmpty(end_date)){
        	appendWhere(sql);
        	sql.append(" and login_time <=? ");
			params.add(end_date+" 23:59:59");
		}
		
		//加上排序
		sql.append(" order by login_time desc ");
		super.queryPager(sql.toString(), new BeanListHandler<MemberLoginLog>(MemberLoginLog.class), params, page);
		return page;
		
	}
	
	/**
	 * 保存日志
	 */
	public boolean saveLoginInfo(MemberLoginLog o) {
		if(insert(o)>0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 删除日志
	 * @param lg
	 * @return
	 */
	public boolean trashMemberLoginLog(MemberLoginLog lg) {
		int rst = delete(lg);
		if(rst>0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 批量删除日志
	 * @param list
	 * @return
	 */
	public boolean trashMemberLoginLogList(List<MemberLoginLog> list) {
		int rst [] = deleteBatch(list);
		if(rst.length>0 && rst.length==list.size()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 清空日志
	 * @return
	 */
	public boolean truncateMemberLoginLog(){
		StringBuffer sql = new StringBuffer(" truncate table member_login_log ");
		List<Object> params = new ArrayList<Object>();
		int rst = memory.update(sql, params);
		if(rst>=0){
			return true;
		}else{
			return false;
		}
	}
	
	public List<MemberLoginLog> findNotIpLocationMemberLoginLog(int start, int limit) {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sql.append("select * from ").append(getCurrentTableName());
		sql.append(" where ip_location is null");
		// 加上排序
		sql.append(" order by login_time desc limit ?,?");
		params.add(start);
		params.add(limit);
		return super.memory.query(sql,  new BeanListHandler<MemberLoginLog>(MemberLoginLog.class), params);
	}
	
	public boolean updateIpLocation(String ipLocation, Integer id) {
		List<Object> params = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("update " + getCurrentTableName() + " set ip_location =? where id=?");
		params.add(ipLocation);
		params.add(id);
		return memory.update(sql, params)>=0;
	}
	
}
