package ddc.server.model.user;

public class Admin extends User {
	protected Admin(Builder builder) {
		super(builder);
	}

	public static class Builder extends UserBuilder<Admin, Builder> {
		@Override
		public Admin build() {
			return new Admin(this);
		}
	}
}