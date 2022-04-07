/*
 * Copyright (C) 2021 ejara.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package de.dfki.asr.ajan.knowledge;

import de.dfki.asr.ajan.common.TripleDataBase;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.rio.RDFFormat;


public class LocalServicesBeliefBase extends AbstractBeliefBase implements TripleDataBase {
	private final TripleDataBase localServRepo;

	public LocalServicesBeliefBase (final TripleDataBase param) {
		localServRepo = param;
	}

	@Override
	public Repository initialize() {
		return localServRepo.getInitializedRepository();
	}

	@Override
	public void add(final Model m) {
		localServRepo.add(m);
	}

	@Override
	public void add(final InputStream stream, final RDFFormat format) throws IOException {
		localServRepo.add(stream, format);
	}

	@Override
	public void clear() {
		localServRepo.clear();
	}

	@Override
	public Repository getInitializedRepository() {
		return localServRepo.getInitializedRepository();
	}

	@Override
	public URL getSparqlEndpoint() {
		return localServRepo.getSparqlEndpoint();
	}

	@Override
	public URL getSparqlUpdateEndpoint() {
		return localServRepo.getSparqlUpdateEndpoint();
	}

	@Override
	public String getId() {
		return localServRepo.getId();
	}

}
