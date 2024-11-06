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

import java.io.IOException;
import java.text.ParseException;

import be.nabu.glue.api.Script;
import be.nabu.glue.api.ScriptFilter;

public class TestCaseFilter implements ScriptFilter {
	@Override
	public boolean accept(Script script) {
		try {
			if (script.getRoot() != null && script.getRoot().getContext() != null && script.getRoot().getContext().getAnnotations().containsKey("testcase")) {
				String string = script.getRoot().getContext().getAnnotations().get("testcase");
				return string == null || string.equalsIgnoreCase("true");
			}
			return false;
		}
		catch (IOException e) {
			e.printStackTrace(System.err);
			return false;
		}
		catch (ParseException e) {
			e.printStackTrace(System.err);
			return false;
		}
		catch (RuntimeException e) {
			e.printStackTrace(System.err);
			return false;
		}
	}
}
