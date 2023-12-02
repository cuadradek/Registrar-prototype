package cz.metacentrum.registrar.service.idm.perun.modules;

import cz.metacentrum.perun.openapi.PerunRPC;
import cz.metacentrum.registrar.persistence.entity.FormModule;
import cz.metacentrum.registrar.service.idm.perun.PerunEnhancedRPC;
import cz.metacentrum.registrar.service.idm.perun.PerunHttp;

public abstract class PerunFormModule implements FormModule {
	protected final PerunHttp perunHttp;
	protected final PerunEnhancedRPC perunRPC;

	protected PerunFormModule(PerunHttp perunHttp, PerunEnhancedRPC perunRPC) {
		this.perunHttp = perunHttp;
		this.perunRPC = perunRPC;
	}
//	protected PerunHttp perunHttp = PerunHttp.getInstance();

}
