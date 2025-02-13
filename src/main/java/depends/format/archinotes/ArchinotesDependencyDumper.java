/*
MIT License

Copyright (c) 2018-2019 Gang ZHANG

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package depends.format.archinotes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import depends.entity.CandidateTypes;
import depends.entity.Entity;
import depends.entity.FileEntity;
import depends.entity.TypeEntity;
import depends.entity.repo.EntityRepo;
import depends.format.archinotes.model.Model;
import depends.format.archinotes.model.ModelElement;
import depends.format.archinotes.model.ModelRelation;
import depends.matrix.core.DependencyDetail;
import depends.relations.Relation;

import static depends.deptypes.DependencyType.POSSIBLE_DEP;

public class ArchinotesDependencyDumper {

	private final EntityRepo entityRepo;
	private Model model;

	public ArchinotesDependencyDumper(EntityRepo entityRepo) {
		this.entityRepo = entityRepo;
	}

	private boolean outputLevelMatch(Entity entity) {
		if (entity instanceof TypeEntity && 
		    !(entity instanceof FileEntity))
			return true;
		return false;
	}

	private int upToOutputLevelEntityId(EntityRepo entityRepo, Entity entity) {
		Entity ancestor = getAncestorOfType(entity);
		if (ancestor == null) {
			return -1;
		}
		if (!ancestor.inScope())
			return -1;
		return ancestor.getId();
	}

	public Entity getAncestorOfType(Entity fromEntity) {
		while (fromEntity != null) {
			if (outputLevelMatch(fromEntity))
				return fromEntity;
			if (fromEntity.getParent() == null)
				return null;
			fromEntity = fromEntity.getParent();
		}
		return null;
	}

	private List<Entity> expandEntity(Entity relatedEntity) {
		List<Entity> entities = new ArrayList<>();
		if (relatedEntity instanceof CandidateTypes) {
			entities = Collections.unmodifiableList((List) ((CandidateTypes) relatedEntity).getCandidateTypes());
		}else {
			entities.add(relatedEntity);
		}
		return entities;
	}

	private void build() {
		model = new Model();
		Map<String, ModelElement> nodes = new HashMap<>();
		Map<String, ModelRelation> edges = new HashMap<>();

		Iterator<Entity> iterator = entityRepo.entityIterator();
		System.out.println("Start create archinotes data....");
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (!entity.inScope())
				continue;

			if (outputLevelMatch(entity)) {
				Entity parentEntity = getAncestorOfType(entity.getParent());
				nodes.put(String.valueOf(entity.getId()), ModelElement.buildElement(entity,parentEntity));
			}
			int entityFrom = upToOutputLevelEntityId(entityRepo, entity);
			if (entityFrom == -1)
				continue;
				
			for (Relation relation : entity.getRelations()) {
				Entity relatedEntity = relation.getEntity();
				if (relatedEntity == null)
					continue;
				List<Entity> relatedEntities = expandEntity(relatedEntity);
				relatedEntities.forEach(theEntity->{
					if (theEntity.getId()>=0) {
						int entityTo = upToOutputLevelEntityId(entityRepo,theEntity);
						if (entityTo!=-1 && entityFrom!=entityTo) {
							ModelRelation modelRelation = ModelRelation.build(entityFrom,entityTo,relation);
							edges.put(modelRelation.getId(), modelRelation);
						}
					}
				});
			}
		}
		System.out.println("Finish create archinotes data....");

		model.setNodes(nodes);
		model.setEdges(edges);
	}

	public void dump(String jsonFileName) {
		build();
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("write to ...."+jsonFileName);
			mapper.writerWithDefaultPrettyPrinter().writeValue(new File(jsonFileName), model);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
