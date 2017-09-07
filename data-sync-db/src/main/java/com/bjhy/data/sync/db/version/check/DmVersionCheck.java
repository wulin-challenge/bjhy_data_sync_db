package com.bjhy.data.sync.db.version.check;

import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.VersionCheck;

/**
 * oracle的版本检测实现
 * @author wubo
 *
 */
public class DmVersionCheck implements VersionCheck{
	
	private SyncLogicEntity syncLogicEntity;
	
	public DmVersionCheck(SyncLogicEntity syncLogicEntity) {
		super();
		this.syncLogicEntity = syncLogicEntity;
	}

	@Override
	public String getAddVersionCheckColumnSql() {
		String syncVersionCheck = VersionCheckCore.SYNC_VERSION_CHECK;
		String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
		String alterSql = "alter table "+toTableName+" add ("+syncVersionCheck+" varchar2(255))";
		return alterSql;
	}

	@Override
	public String getToDeleteByVersionCheck() {
		String syncVersionCheck = VersionCheckCore.SYNC_VERSION_CHECK;
		String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
		String toDeleteSql = "DELETE FROM "+toTableName+" WHERE "+syncVersionCheck+" in(:"+syncVersionCheck+") or "+syncVersionCheck+" is null or "+syncVersionCheck+"=''";
		return toDeleteSql;
	}

}

