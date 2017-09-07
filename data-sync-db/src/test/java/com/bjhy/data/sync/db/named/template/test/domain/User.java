package com.bjhy.data.sync.db.named.template.test.domain;


public class User {
		private String id;
		private String username;
		private String password;
		private Dept d;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public Dept getD() {
			return d;
		}
		public void setD(Dept d) {
			this.d = d;
		}
		
	}