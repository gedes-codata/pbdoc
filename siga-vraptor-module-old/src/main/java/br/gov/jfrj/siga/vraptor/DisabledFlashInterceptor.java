package br.gov.jfrj.siga.vraptor;

import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.interceptor.FlashInterceptor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.ResourceMethod;

@Component
public class DisabledFlashInterceptor extends FlashInterceptor {

	public DisabledFlashInterceptor(HttpSession session, Result result,
			MutableResponse response) {
		super(session, result, response);
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return false;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object resourceInstance) throws InterceptionException {
		return;
	}

}