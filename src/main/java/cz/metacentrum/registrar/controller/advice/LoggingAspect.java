package cz.metacentrum.registrar.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

	@Pointcut("within(cz.metacentrum.registrar.controller..*)")
	public void controllerPackagePointcut() {

	}

	@Pointcut("within(cz.metacentrum.registrar.service..*)" +
			" || within(cz.metacentrum.registrar.repository..*)")
	public void applicationPackagePointcut() {

	}

	@Pointcut("within(@org.springframework.stereotype.Repository *)" +
			" || within(@org.springframework.stereotype.Service *)" +
			" || within(@org.springframework.web.bind.annotation.RestController *)")
	public void springBeanPointcut() {

	}

	@AfterThrowing(pointcut = "controllerPackagePointcut()", throwing = "e")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
		log.error("Exception in {}.{}()", joinPoint.getSignature().getDeclaringTypeName(),
				joinPoint.getSignature().getName(), e);
	}

	@Around("controllerPackagePointcut() && springBeanPointcut()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
				joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));

		Object result = joinPoint.proceed();

		log.info("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
				joinPoint.getSignature().getName(), result);

		return result;
	}
}
