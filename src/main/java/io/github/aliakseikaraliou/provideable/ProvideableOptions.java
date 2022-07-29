package io.github.aliakseikaraliou.provideable;

import javax.lang.model.type.TypeMirror;

class ProvideableOptions {
	private final String packageName;
	private final String typeName;
	private final String methodName;

	private final TypeMirror target;

	private ProvideableOptions(String packageName, String typeName, String methodName, TypeMirror target) {
		this.packageName = packageName;
		this.typeName = typeName;
		this.methodName = methodName;
		this.target = target;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getFullName() {
		return packageName + "." + typeName;
	}

	public TypeMirror getTarget() {
		return target;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String packageName;
		private String typeName;
		private String methodName;

		private TypeMirror target;

		private Builder() {
		}

		public Builder setPackageName(String packageName) {
			this.packageName = packageName;
			return this;
		}

		public Builder setTypeName(String typeName) {
			this.typeName = typeName;
			return this;
		}

		public Builder setMethodName(String methodName) {
			this.methodName = methodName;
			return this;
		}


		public Builder setTarget(TypeMirror target) {
			this.target = target;
			return this;
		}

		public Builder setProvideable(Provideable provideable) {
			if (!provideable.name().isEmpty()) {
				this.typeName = provideable.name();
			}

			if (!provideable.methodName().isEmpty()) {
				this.methodName = provideable.methodName();
			}

			if (!provideable.packageName().isEmpty()) {
				this.packageName = provideable.packageName();
			}

			return this;
		}

		public ProvideableOptions build() {
			return new ProvideableOptions(packageName, typeName, methodName, target);
		}


	}
}
