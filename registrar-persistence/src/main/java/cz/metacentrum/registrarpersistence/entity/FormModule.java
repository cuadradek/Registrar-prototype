package cz.metacentrum.registrarpersistence.entity;

// maybe implement this as abstract class and have config options as the only field, maybe friendly name as well..
public interface FormModule {
	Form beforeApprove();
	Form onApprove();
	Form onReject();
}
