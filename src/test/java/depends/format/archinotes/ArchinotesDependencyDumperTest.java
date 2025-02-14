package depends.format.archinotes;

import static org.junit.Assert.*;

import depends.deptypes.DependencyType;
import org.junit.Before;
import org.junit.Test;

import depends.entity.repo.EntityRepo;
import depends.entity.repo.InMemoryEntityRepo;
import depends.format.archinotes.model.Model;
import depends.format.archinotes.model.ModelElement;
import depends.entity.TypeEntity;
import depends.entity.GenericName;
import depends.relations.Relation;
import depends.format.archinotes.model.ModelRelation;
import depends.format.archinotes.model.ModelElement.Method;
import depends.entity.FunctionEntity;
import java.util.List;

public class ArchinotesDependencyDumperTest {
    
    private EntityRepo entityRepo;
    private ArchinotesDependencyDumper dumper;
    
    @Before
    public void setUp() {
        entityRepo = new InMemoryEntityRepo();
        dumper = new ArchinotesDependencyDumper(entityRepo);
    }
    
    @Test
    public void should_return_empty_model_for_empty_repo() {
        // when
        dumper.build();
        Model model = dumper.getModel();
        
        // then
        assertNotNull(model);
        assertNotNull(model.getNodes());
        assertNotNull(model.getEdges());
        assertTrue(model.getNodes().isEmpty());
        assertTrue(model.getEdges().isEmpty());
    }
    
    @Test
    public void should_create_dependency_between_two_types() {
        // create two types
        TypeEntity typeA = new TypeEntity(GenericName.build("TypeA"), "class", null, 1);
        TypeEntity typeB = new TypeEntity(GenericName.build("TypeB"), "class", null, 2);
        
        // add them to repo
        entityRepo.add(typeA);
        entityRepo.add(typeB);
        
        // create dependency: TypeA -> TypeB
        Relation relation = new Relation("DEPEND", typeB, null, false);
        typeA.addRelation(relation);
        
        // when
        dumper.build();
        Model model = dumper.getModel();
        
        // then
        assertNotNull(model);
        assertEquals(2, model.getNodes().size());
        assertEquals(1, model.getEdges().size());
        
        // verify nodes
        assertTrue(model.getNodes().containsKey("1")); // TypeA
        assertTrue(model.getNodes().containsKey("2")); // TypeB
        
        // verify edge
        ModelRelation edge = model.getEdges().values().iterator().next();
        assertEquals("1", edge.getSourceModelId());  // from TypeA
        assertEquals("2", edge.getTargetModelId());    // to TypeB
        assertEquals("Dependency", edge.getType());  // default type mapping
    }
    
    @Test
    public void should_create_dependency_between_types_when_function_depends() {
        // create types as containers
        TypeEntity typeA = new TypeEntity(GenericName.build("TypeA"), "class", null, 1);
        TypeEntity typeB = new TypeEntity(GenericName.build("TypeB"), "class", null, 2);
        
        // create functions inside types with return type
        FunctionEntity funcA = new FunctionEntity(
            GenericName.build("funcA"), 
            typeA,  // parent
            3,      // id
            GenericName.build("void")  // return type
        );
        
        FunctionEntity funcB = new FunctionEntity(
            GenericName.build("funcB"), 
            typeB,  // parent
            4,      // id
            GenericName.build("void")  // return type
        );
        
        // add all entities to repo
        entityRepo.add(typeA);
        entityRepo.add(typeB);
        entityRepo.add(funcA);
        entityRepo.add(funcB);
        
        // create dependency: funcA -> funcB
        Relation relation = new Relation("CALL", funcB, null, false);
        funcA.addRelation(relation);
        
        // when
        dumper.build();
        Model model = dumper.getModel();
        
        // then
        assertNotNull(model);
        assertEquals(2, model.getNodes().size());  // only type level entities
        assertEquals(1, model.getEdges().size());
        
        // verify nodes (only types should be present)
        assertTrue(model.getNodes().containsKey("1")); // TypeA
        assertTrue(model.getNodes().containsKey("2")); // TypeB
        assertFalse(model.getNodes().containsKey("3")); // funcA should not be present
        assertFalse(model.getNodes().containsKey("4")); // funcB should not be present
        
        // verify edge is created at type level
        ModelRelation edge = model.getEdges().values().iterator().next();
        assertEquals("1", edge.getSourceModelId());  // from TypeA
        assertEquals("2", edge.getTargetModelId());  // to TypeB
        assertEquals("Dependency", edge.getType());  // mapped to Dependency
    }
    
    @Test
    public void should_include_methods_in_type_nodes() {
        // create type as container
        TypeEntity typeA = new TypeEntity(GenericName.build("TypeA"), "class", null, 1);
        
        // create two functions inside type
        FunctionEntity funcA1 = new FunctionEntity(
            GenericName.build("funcA1"), 
            typeA,
            2,
            GenericName.build("void")
        );
        
        FunctionEntity funcA2 = new FunctionEntity(
            GenericName.build("funcA2"), 
            typeA,
            3,
            GenericName.build("String")
        );
        typeA.addFunction(funcA1);
        typeA.addFunction(funcA2);
        
        // add all entities to repo
        entityRepo.add(typeA);
        entityRepo.add(funcA1);
        entityRepo.add(funcA2);
        
        // when
        dumper.build();
        Model model = dumper.getModel();
        
        // then
        assertNotNull(model);
        assertEquals(1, model.getNodes().size());
        
        // verify type node
        ModelElement typeNode = model.getNodes().get("1");
        assertNotNull(typeNode);
        assertEquals("TypeA", typeNode.getName());
        assertEquals("class", typeNode.getType());
        
        // verify methods in type node
        List<Method> methods = typeNode.getMethods();
        assertNotNull(methods);
        assertEquals(2, methods.size());
    }
    
    @Test
    public void should_keep_original_type_for_high_priority_relations() {
        // create two types
        TypeEntity typeA = new TypeEntity(GenericName.build("TypeA"), "class", null, 1);
        TypeEntity typeB = new TypeEntity(GenericName.build("TypeB"), "class", null, 2);
        
        entityRepo.add(typeA);
        entityRepo.add(typeB);
        
        // create high priority relations
        Relation containRelation = new Relation(DependencyType.CONTAIN, typeB, null, false);
        Relation inheritRelation = new Relation(DependencyType.INHERIT, typeB, null, false);
        Relation implementRelation = new Relation(DependencyType.IMPLEMENT, typeB, null, false);
        
        typeA.addRelation(containRelation);
        typeA.addRelation(inheritRelation);
        typeA.addRelation(implementRelation);
        
        // when
        dumper.build();
        Model model = dumper.getModel();
        
        // then
        assertEquals(3, model.getEdges().size());
        
        // verify each relation keeps its original type
        boolean hasContain = false;
        boolean hasInherit = false;
        boolean hasImplement = false;
        
        for (ModelRelation edge : model.getEdges().values()) {
            assertEquals("1", edge.getSourceModelId());
            assertEquals("2", edge.getTargetModelId());
            
            switch (edge.getType()) {
                case DependencyType.CONTAIN:
                    hasContain = true;
                    break;
                case DependencyType.INHERIT:
                    hasInherit = true;
                    break;
                case DependencyType.IMPLEMENT:
                    hasImplement = true;
                    break;
            }
        }
        
        assertTrue("Should have CONTAIN relation", hasContain);
        assertTrue("Should have INHERIT relation", hasInherit);
        assertTrue("Should have IMPLEMENT relation", hasImplement);
    }
    
    @Test
    public void should_map_normal_relations_to_dependency() {
        // create two types
        TypeEntity typeA = new TypeEntity(GenericName.build("TypeA"), "class", null, 1);
        TypeEntity typeB = new TypeEntity(GenericName.build("TypeB"), "class", null, 2);
        
        entityRepo.add(typeA);
        entityRepo.add(typeB);
        
        // create various normal relations
        Relation callRelation = new Relation("CALL", typeB, null, false);
        Relation useRelation = new Relation("USE", typeB, null, false);
        Relation createRelation = new Relation("CREATE", typeB, null, false);
        
        typeA.addRelation(callRelation);
        typeA.addRelation(useRelation);
        typeA.addRelation(createRelation);
        
        // when
        dumper.build();
        Model model = dumper.getModel();
        
        // then
        assertEquals(1, model.getEdges().size());  // multiple normal relations should be merged
        
        // verify the relation is mapped to Dependency
        ModelRelation edge = model.getEdges().values().iterator().next();
        assertEquals("1", edge.getSourceModelId());
        assertEquals("2", edge.getTargetModelId());
        assertEquals("Dependency", edge.getType());
    }
    
} 