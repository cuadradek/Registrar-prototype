package cz.metacentrum.registrar.service.idm.perun.modules;

import cz.metacentrum.registrar.persistence.entity.FormModule;
import cz.metacentrum.registrar.service.idm.perun.PerunHttp;

public abstract class PerunFormModule implements FormModule {
	protected PerunHttp perunHttp = PerunHttp.getInstance();
}
