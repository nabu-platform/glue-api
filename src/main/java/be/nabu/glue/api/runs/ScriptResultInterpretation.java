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

package be.nabu.glue.api.runs;

public interface ScriptResultInterpretation {
	/**
	 * Gets the variance of this script result versus a reference point [0,1[
	 */
	public double getActualVariance();
	/**
	 * Gets the allowed variance for this script result [0,1[
	 * This is both up and under, so if the allowed variance is 0.2 and the variance is -0.3, it will still be flagged 
	 * but likely with less urgency than when it is +0.3 
	 */
	public double getAllowedVariance();
}
