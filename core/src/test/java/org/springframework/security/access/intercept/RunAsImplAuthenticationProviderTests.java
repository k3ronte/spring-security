/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.access.intercept;

import static org.junit.Assert.*;

import org.junit.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * Tests {@link RunAsImplAuthenticationProvider}.
 */
public class RunAsImplAuthenticationProviderTests {

	@Test(expected = BadCredentialsException.class)
	public void testAuthenticationFailDueToWrongKey() {
		RunAsUserToken token = new RunAsUserToken("wrong_key", "Test", "Password",
				AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO"),
				UsernamePasswordAuthenticationToken.class);
		RunAsImplAuthenticationProvider provider = new RunAsImplAuthenticationProvider();
		provider.setKey("hello_world");

		provider.authenticate(token);
	}

	@Test
	public void testAuthenticationSuccess() {
		RunAsUserToken token = new RunAsUserToken("my_password", "Test", "Password",
				AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO"),
				UsernamePasswordAuthenticationToken.class);
		RunAsImplAuthenticationProvider provider = new RunAsImplAuthenticationProvider();
		provider.setKey("my_password");

		Authentication result = provider.authenticate(token);

		Assert.assertTrue("Should have returned RunAsUserToken",
				result instanceof RunAsUserToken);

		RunAsUserToken resultCast = (RunAsUserToken) result;
		assertEquals("my_password".hashCode(), resultCast.getKeyHash());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStartupFailsIfNoKey() throws Exception {
		RunAsImplAuthenticationProvider provider = new RunAsImplAuthenticationProvider();

		provider.afterPropertiesSet();
	}

	@Test
	public void testStartupSuccess() throws Exception {
		RunAsImplAuthenticationProvider provider = new RunAsImplAuthenticationProvider();
		provider.setKey("hello_world");
		assertEquals("hello_world", provider.getKey());
		provider.afterPropertiesSet();
	}

	@Test
	public void testSupports() {
		RunAsImplAuthenticationProvider provider = new RunAsImplAuthenticationProvider();
		assertTrue(provider.supports(RunAsUserToken.class));
		assertTrue(!provider.supports(TestingAuthenticationToken.class));
	}
}
