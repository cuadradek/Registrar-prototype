package cz.metacentrum.registrar.service.idm.perun.modules;

import cz.metacentrum.registrar.persistence.entity.FormModule;
import cz.metacentrum.registrar.service.idm.perun.PerunHttp;

public abstract class PerunFormModule implements FormModule {
	protected final PerunHttp perunHttp;

	protected PerunFormModule(PerunHttp perunHttp) {
		this.perunHttp = perunHttp;
	}
//	protected PerunHttp perunHttp = PerunHttp.getInstance();

}
