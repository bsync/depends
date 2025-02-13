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

package depends.format.archinotes.model;

import java.util.ArrayList;
import java.util.List;

import depends.entity.Entity;
import depends.entity.FileEntity;
import depends.entity.FunctionEntity;
import depends.entity.PackageEntity;
import depends.entity.TypeEntity;
import depends.entity.VarEntity;

public class ModelElement {
    private String id;
    private String type;
    private String name;
    private String parentId;
    private List<Prop> props;
    private List<Method> methods;
    private String stereoType;

    public static class Prop {
        private String name;
        private String description;
        private ModelType type;
        private String visibility;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public ModelType getType() {
            return type;
        }

        public void setType(ModelType type) {
            this.type = type;
        }

        public String getVisibility() {
            return visibility;
        }

        public void setVisibility(String visibility) {
            this.visibility = visibility;
        }
    }

    public static class Parameter {
        private String id;
        private String name;
        private ModelType type;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ModelType getType() {
            return type;
        }

        public void setType(ModelType type) {
            this.type = type;
        }
    }

    public static class Method {
        private String id;
        private String name;
        private ModelType type;
        private List<Parameter> parameters;
        private String description;
        private String visibility;


        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ModelType getType() {
            return type;
        }

        public void setType(ModelType type) {
            this.type = type;
        }

        public List<Parameter> getParameters() {
            return parameters;
        }

        public void setParameters(List<Parameter> parameters) {
            this.parameters = parameters;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVisibility() {
            return visibility;
        }

        public void setVisibility(String visibility) {
            this.visibility = visibility;
        }
    }

    public ModelElement(String id, String type, String name, String parentId) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<Prop> getProps() {
        return props;
    }

    public void setProps(List<Prop> props) {
        this.props = props;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public String getStereoType() {
        return stereoType;
    }

    public void setStereoType(String stereoType) {
        this.stereoType = stereoType;
    }

    public static String getType(Entity entity) {
        if (entity instanceof FileEntity)
            return "file";
        if (entity instanceof PackageEntity)
            return "package";
        if (entity instanceof TypeEntity)
            return ((TypeEntity) entity).getStereoType();
        return "unknown";
    }

    public static ModelElement buildElement(Entity entity, Entity parentEntity) {
        ModelElement element = new ModelElement(
                String.valueOf(entity.getId()),
                getType(entity),
                entity.getRawName().getName(),
                String.valueOf(parentEntity == null ? -1 : entity.getParent().getId()));

        element.setStereoType(getType(entity));
        element.setProps(getProps(entity));
        element.setMethods(getMethods(entity));
        return element;
    }

    private static List<Method> getMethods(Entity entity) {
        List<Method> methods = new ArrayList<>();
        if (entity instanceof TypeEntity) {
            TypeEntity typeEntity = (TypeEntity) entity;
            for (FunctionEntity function : typeEntity.getFunctions()) {
                Method method = new Method();
                method.setId(String.valueOf(function.getId()));
                method.setName(function.getRawName().getName());
                method.setVisibility(function.getVisibility());
                methods.add(method);
            }
        }
        return methods;
    }

    private static List<Prop> getProps(Entity entity) {
        List<Prop> props = new ArrayList<>();
        if (entity instanceof TypeEntity) {
            TypeEntity typeEntity = (TypeEntity) entity;
            for (VarEntity var : typeEntity.getVars()) {
                Prop prop = new Prop();
                prop.setName(var.getRawName().getName());
                prop.setVisibility(var.getVisibility());
                props.add(prop);
            }
        }
        return props;
    }
}
