package cz.metacentrum.registrar.service.iam.perun.modules;

import cz.metacentrum.registrar.service.iam.FormModule;
import cz.metacentrum.registrar.service.iam.perun.client.PerunEnhancedRPC;

public abstract class PerunFormModule implements FormModule {
	protected final PerunEnhancedRPC perunRPC;

	protected PerunFormModule(PerunEnhancedRPC perunRPC) {
		this.perunRPC = perunRPC;
	}

}
