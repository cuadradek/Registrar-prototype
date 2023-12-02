package cz.metacentrum.registrar.service.iam.perun.modules;

import cz.metacentrum.registrar.service.iam.FormModule;
import cz.metacentrum.registrar.service.iam.perun.client.PerunEnhancedRPC;
import cz.metacentrum.registrar.service.iam.perun.client.PerunHttp;

public abstract class PerunFormModule implements FormModule {
	protected final PerunHttp perunHttp;
	protected final PerunEnhancedRPC perunRPC;

	protected PerunFormModule(PerunHttp perunHttp, PerunEnhancedRPC perunRPC) {
		this.perunHttp = perunHttp;
		this.perunRPC = perunRPC;
	}
//	protected PerunHttp perunHttp = PerunHttp.getInstance();

}
