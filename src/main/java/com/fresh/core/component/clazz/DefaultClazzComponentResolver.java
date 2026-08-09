package com.fresh.core.component.clazz;

import com.fresh.core.component.AbstractComponentResolver;
import com.fresh.core.component.Component;

import java.util.*;

/**
 * ClazzComponentResolver 的默认实现
 */
public class DefaultClazzComponentResolver extends AbstractComponentResolver<Class<?>>
                                           implements ClazzComponentResolver {

    public DefaultClazzComponentResolver(Component<Class<?>> component) {
        super(component);
    }


//
// 实现 ClazzComponentResolver
//
    @Override
    public List<Class<?>> getAllSuperClass() {
        List<Class<?>> result = new ArrayList<>();
        List<Component<Class<?>>> children = component.getAllChild();
        getAllSuperClass(result, children);
        return result;
    }

    private void getAllSuperClass(List<Class<?>> listResult, List<Component<Class<?>>> childs) {
        for(Component<Class<?>> child : childs) {
            Class<?> entity = child.getEntity();
            if(entity != null && !entity.isInterface()) {
                listResult.add(entity);
                getAllSuperClass(listResult, child.getAllChild());
                break;
            }
        }
    }

    @Override
    public List<Class<?>> getAllInterfaces() {
        List<Class<?>> result = new ArrayList<>();
        List<Component<Class<?>>> children = component.getAllChild();
        Set<Class<?>> setLinked = getAllInterfacesBFS(children);
        result.addAll(setLinked);
        return result;
    }
    private Set<Class<?>> getAllInterfacesBFS(List<Component<Class<?>>> children) {
        Set<Class<?>> listResult = new LinkedHashSet<>();
        Queue<Component<Class<?>>> queueList = new LinkedList<>();
        children.forEach(queueList::offer);
        while(!queueList.isEmpty()) {
            Component<Class<?>> currentNode = queueList.poll();
            Class<?> entity = currentNode.getEntity();
            if(entity != null && entity.isInterface()) {
                listResult.add(entity);
            }
            currentNode.getAllChild().forEach(queueList::offer);
        }
        return listResult;
    }


}
