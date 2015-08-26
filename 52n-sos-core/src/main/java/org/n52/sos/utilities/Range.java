/***************************************************************
 Copyright (C) 2008
 by 52 North Initiative for Geospatial Open Source Software GmbH

 Contact: Andreas Wytzisk
 52 North Initiative for Geospatial Open Source Software GmbH
 Martin-Luther-King-Weg 24
 48155 Muenster, Germany
 info@52north.org

 This program is free software; you can redistribute and/or modify it under 
 the terms of the GNU General Public License version 2 as published by the 
 Free Software Foundation.

 This program is distributed WITHOUT ANY WARRANTY; even without the implied
 WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 this program (see gnu-gpl v2.txt). If not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 visit the Free Software Foundation web page, http://www.fsf.org.

 Author: <LIST OF AUTHORS/EDITORS>
 Created: <CREATION DATE>
 Modified: <DATE OF LAST MODIFICATION (optional line)>
***************************************************************/

package org.n52.sos.utilities;

/**
 * class represents a range
 * 
 * @author Stephan Kuenster
 * 
 */
public class Range {

	/** from value */
	private int from = 0;
	
	/** to value */
	private int to = Integer.MAX_VALUE;

	/**
	 * constructor
	 * 
	 * @param start
	 * @param end
	 */
	public Range (int start, int end) {
		this.from = start;
		this.to = end;
	}

	/**
	 * returns true if a given value is contained in range
	 * 
	 * @param value
	 * @return boolean true if value is contained in range, else false
	 */
	public boolean contains (int value) {
		if (from  <= value && to >= value) {
			return true;
		} else {
			return false;
		}
	}
}
