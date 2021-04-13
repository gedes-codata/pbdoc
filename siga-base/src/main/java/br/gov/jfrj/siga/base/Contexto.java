/*******************************************************************************
 * Copyright (c) 2006 - 2011 SJRJ.
 * 
 *     This file is part of SIGA.
 * 
 *     SIGA is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     SIGA is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with SIGA.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package br.gov.jfrj.siga.base;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.stripToNull;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

public class Contexto {
	public static Object resource(String name) {
		Context initContext = null;
		Context envContext = null;

		try {
			return System.getProperty(name);
		} catch (Exception ex) {
			try {
				initContext = new InitialContext();
				envContext = (Context) initContext.lookup("java:/comp/env");
				Object value = envContext.lookup(name);
				if (value != null)
					return value;
			} catch (final NamingException e) {
			}
		}
		return null;
	}
	
	public static String urlBase(HttpServletRequest request) {
		return urlBase(request, true);
	}

	public static String urlBase(HttpServletRequest request, boolean considerarPropriedadeSigaBaseUrl) {
		String baseUrl = stripToNull(System.getProperty("siga.base.url"));
		String visibleSchema = null;
		if (baseUrl != null) {
			if (considerarPropriedadeSigaBaseUrl) {
				return baseUrl;
			}
			visibleSchema = baseUrl.substring(0, baseUrl.indexOf("://"));
		}
		return baseUrlFrom(request, visibleSchema);
	}

	public static String baseUrlFrom(HttpServletRequest request, String visibleScheme) {
		StringBuilder baseUrlBuilder = new StringBuilder()
			.append(isNotEmpty(visibleScheme) ? visibleScheme : request.getScheme())
			.append("://")
			.append(request.getServerName());

		int requestPort = request.getServerPort();
		if (80 != requestPort) {
			baseUrlBuilder.append(":").append(requestPort);
		}

		return baseUrlBuilder.toString();
	}

}
