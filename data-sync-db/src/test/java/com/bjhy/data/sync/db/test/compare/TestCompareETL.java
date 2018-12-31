package com.bjhy.data.sync.db.test.compare;

import org.junit.Test;

import com.bjhy.data.sync.db.core.BaseRun;
import com.bjhy.data.sync.db.domain.BaseRunEntity;
import com.bjhy.data.sync.db.domain.SingleRunEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunSync;

public class TestCompareETL {
	
	@Test
	public void incrementalSync(){
		BaseRun baseRun = BaseRun.getInstance();
		baseRun.baseForRun(new BaseRunEntity("from_task", "to_task"), new ForRunSync(){
			@Override
			public void singleRun(SingleRunEntity singleRunEntity) {
				long start = System.currentTimeMillis();
				CompareHelper.incrementalSync(singleRunEntity, "test_copy_user", "test_copy_user", "id", null);
				long end = System.currentTimeMillis();
				System.out.println("同步时间:"+(end-start));
			}
		});
	}

}
