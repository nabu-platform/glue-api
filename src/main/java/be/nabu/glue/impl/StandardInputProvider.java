/*
* Copyright (C) 2014 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.glue.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import be.nabu.glue.api.InputProvider;

public class StandardInputProvider implements InputProvider {

	@Override
	public String input(String message, boolean secret, String defaultValue) throws IOException {
		if (message != null) {
			System.out.print(message);
		}
		String result;
		if (System.console() != null) {
			if (secret) {
				result = new String(System.console().readPassword());
			}
			else {
				result = System.console().readLine();
			}
		}
		else {
			result = new BufferedReader(new InputStreamReader(System.in)).readLine();
		}
		if (result == null || result.trim().isEmpty()) {
			result = defaultValue;
		}
		return result;
	}

}
