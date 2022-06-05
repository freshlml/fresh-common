package com.fresh.common.component;

import java.util.*;

/**
 * ClazzComponentResolver的默认实现
 */
public class DefaultClazzComponentResolver extends AbstractComponentResolver<Class<?>>
                                           implements ClazzComponentResolver {

    public DefaultClazzComponentResolver(Component<Class<?>> component) {
        super(component);
    }


//
// 实现ClazzComponentResolver
//
    @Override
    public List<Class<?>> getAllSuperClass() {
        List<Class<?>> result = new ArrayList<>();
        List<Component<Class<?>>> childs = component.getChilds();
        getAllSuperClass(result, childs);
        return result;
    }

    private void getAllSuperClass(List<Class<?>> listResult, List<Component<Class<?>>> childs) {
        for(Component<Class<?>> child : childs) {
            if(!child.getInfo().isInterface()) {
                listResult.add(child.getInfo());
                getAllSuperClass(listResult, child.getChilds());
                break;
            }
        }
    }

    @Override
    public List<Class<?>> getAllInterfaces() {
        List<Class<?>> result = new ArrayList<>();
        List<Component<Class<?>>> childs = component.getChilds();
        Set<Class<?>> setLinked = getAllInterfacesBFS(childs);
        result.addAll(setLinked);
        return result;
    }
    private Set<Class<?>> getAllInterfacesBFS(List<Component<Class<?>>> childs) {
        Set<Class<?>> listResult = new LinkedHashSet<>();
        Queue<Component<Class<?>>> queueList = new LinkedList<>();
        childs.forEach(queueList::offer);
        while(!queueList.isEmpty()) {
            Component<Class<?>> currentNode = queueList.poll();
            if(currentNode.getInfo().isInterface()) {
                listResult.add(currentNode.getInfo());
            }
            currentNode.getChilds().forEach(queueList::offer);
        }
        return listResult;
    }


}
