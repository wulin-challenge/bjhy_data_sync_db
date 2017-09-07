package com.bjhy.data.sync.db.test;

import com.bjhy.data.sync.db.inter.face.OwnInterface.ForRunThread;
import com.bjhy.data.sync.db.thread.ThreadControl;

/**
 * 测试线程的控制
 * @author wubo
 *
 */
public class TestThreadControl {
	
	public static void main(String[] args) {
		ThreadControl threadControl = new ThreadControl(10);
		
		threadControl.forRunStart(100, new ForRunThread(){
			
			@Override
			public void allThreadBeforeRun(int iterations) {
				System.out.println("start before"+iterations);
			}
//
//			@Override
//			public void currentThreadBeforeRun(int iterations, int i) {
//				System.out.println("currentThreadBeforeRun"+i);
//			}
//
//			@Override
//			public void currentThreadAfterRun(int iterations, int i) {
//				System.out.println("currentThreadAfterRun"+i);
//			}

			@Override
			public void allThreadAfterRun(int iterations) {
				System.out.println("start after"+iterations);
			}

			@Override
			public void currentThreadRunning(int iterations, final int i) {
//				int j = 1/0;
//				System.out.println(i);
				
				ThreadControl threadControl2 = new ThreadControl(10);
				threadControl2.forRunStart(1000, new ForRunThread(){

					@Override
					public void allThreadBeforeRun(int iterations) {
						System.out.println("start before"+iterations+"-->"+i);
					}
					
					@Override
					public void currentThreadRunning(int iterations, final int j) {
//						System.out.println("--"+i+"-->"+j);
						
						ThreadControl threadControl3 = new ThreadControl(10);
						threadControl3.forRunStart(1000, new ForRunThread(){
							@Override
							public void allThreadAfterRun(int iterations) {
								System.out.println("start after"+iterations+"-->"+i+"-->"+j);
								System.out.println((String)getShareParams().get("share")+iterations+"-->"+i+"-->"+j);
							}
							
							@Override
							public void allThreadBeforeRun(int iterations) {
								System.out.println("start before"+iterations+"-->"+i+"-->"+j);
								getShareParams().put("share", "我是共享参数值中的一个");
							}
							
							@Override
							public void currentThreadRunning(int iterations,int k) {
								System.out.println("--"+i+"-->"+j+"-->"+k);
							}
							
						});
						
					}
					
					@Override
					public void allThreadAfterRun(int iterations) {
						System.out.println("start after"+iterations+"-->"+i);
					}
					
				});
				
				
				
			}

			@Override
			public void currentThreadErrorRun(int iterations, int i, Exception e) {
				System.out.println("currentThreadErrorRun"+i);
				System.out.println(e.getMessage());
			}
			
		});
		
		
		
	}

}
