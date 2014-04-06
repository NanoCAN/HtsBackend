/*
 * Copyright (C) 2014
 * Center for Excellence in Nanomedicine (NanoCAN)
 * Molecular Oncology
 * University of Southern Denmark
 * ###############################################
 * Written by:	Markus List
 * Contact: 	mlist'at'health'.'sdu'.'dk
 * Web:			http://www.nanocan.org/miracle/
 * ###########################################################################
 *	
 *	This file is part of MIRACLE.
 *
 *  MIRACLE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with this program. It can be found at the root of the project page.
 *	If not, see <http://www.gnu.org/licenses/>.
 *
 * ############################################################################
 */
package org.nanocan.security

import org.apache.commons.lang.builder.HashCodeBuilder

class PersonRole implements Serializable {

	Person person
	Role role

	boolean equals(other) {
		if (!(other instanceof PersonRole)) {
			return false
		}

		other.person?.id == person?.id &&
			other.role?.id == role?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (person) builder.append(person.id)
		if (role) builder.append(role.id)
		builder.toHashCode()
	}

	static PersonRole get(long personId, long roleId) {
		find 'from PersonRole where person.id=:personId and role.id=:roleId',
			[personId: personId, roleId: roleId]
	}

	static PersonRole create(Person person, Role role, boolean flush = false) {
		new PersonRole(person: person, role: role).save(flush: flush, insert: true)
	}

	static boolean remove(Person person, Role role, boolean flush = false) {
		PersonRole instance = PersonRole.findByPersonAndRole(person, role)
		if (!instance) {
			return false
		}

		instance.delete(flush: flush)
		true
	}

	static void removeAll(Person person) {
		executeUpdate 'DELETE FROM PersonRole WHERE person=:person', [person: person]
	}

	static void removeAll(Role role) {
		executeUpdate 'DELETE FROM PersonRole WHERE role=:role', [role: role]
	}

	static mapping = {
		id composite: ['role', 'person']
		version false
	}
}
