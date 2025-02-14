package depends.format.archinotes;

import depends.deptypes.DependencyType;
import depends.format.archinotes.model.ModelRelation;
import depends.relations.Relation;
import java.util.*;
import java.util.stream.Collectors;

public class RelationBuildStrategy {
    // 关系优先级映射，数字越小优先级越高
    private static final Map<String, Integer> RELATION_PRIORITIES = new HashMap<>();
    static {
        RELATION_PRIORITIES.put(DependencyType.CONTAIN, 1);
        RELATION_PRIORITIES.put(DependencyType.INHERIT, 1);
        RELATION_PRIORITIES.put(DependencyType.IMPLEMENT, 1);
        // ... 其他关系映射不需要，默认 100
    }

    // 关系类型映射，将具体关系映射到更通用的关系
    private static final Map<String, String> RELATION_TYPE_MAPPING = new HashMap<>();
    static {
        // 保持高优先级关系的原始类型
        RELATION_TYPE_MAPPING.put(DependencyType.CONTAIN, DependencyType.CONTAIN);
        RELATION_TYPE_MAPPING.put(DependencyType.INHERIT, DependencyType.INHERIT);
        RELATION_TYPE_MAPPING.put(DependencyType.IMPLEMENT, DependencyType.IMPLEMENT);
        // 其他关系统一映射为 "Dependency"
    }

    public List<ModelRelation> processRelations(int entityFrom, int entityTo, List<Relation> relations) {
        List<ModelRelation> result = new ArrayList<>();
        
        // 分离高优先级关系和普通关系
        List<Relation> highPriorityRelations = relations.stream()
                .filter(r -> RELATION_PRIORITIES.getOrDefault(r.getType(), 100) == 1)
                .collect(Collectors.toList());
                
        if (!highPriorityRelations.isEmpty()) {
            // 处理所有高优先级关系
            for (Relation relation : highPriorityRelations) {
                ModelRelation modelRelation = ModelRelation.build(entityFrom, entityTo, relation);
                // 高优先级关系保持原始类型
                modelRelation.setType(relation.getType());
                result.add(modelRelation);
            }
        } else {
            // 如果没有高优先级关系，使用普通依赖关系
            Relation primaryRelation = relations.get(0);
            ModelRelation modelRelation = ModelRelation.build(entityFrom, entityTo, primaryRelation);
            modelRelation.setType("Dependency");
            result.add(modelRelation);
        }
        
        return result;
    }

} 