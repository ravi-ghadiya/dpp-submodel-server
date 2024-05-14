package com.demo.wrapper.model.edc.policy;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "edctype")
public abstract class Rule extends Identifiable {

    protected String target;
    protected Action action;
    protected String assignee;
    protected String assigner;
    protected List<Constraint> constraints = new ArrayList<>();

    public String getTarget() {
        return target;
    }

    public Action getAction() {
        return action;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public String getAssigner() {
        return assigner;
    }

    public String getAssignee() {
        return assignee;
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitPermission(Permission policy);

        R visitProhibition(Prohibition policy);

        R visitDuty(Duty policy);
    }

    protected abstract static class Builder<T extends Rule, B extends Builder<T, B>> {
        protected T rule;

        public B target(String target) {
            rule.target = target;
            return (B) this;
        }

        public B assigner(String assigner) {
            rule.assigner = assigner;
            return (B) this;
        }

        public B assignee(String assignee) {
            rule.assignee = assignee;
            return (B) this;
        }

        public B action(Action action) {
            rule.action = action;
            return (B) this;
        }

        public B constraint(Constraint constraint) {
            rule.constraints.add(constraint);
            return (B) this;
        }

        public B constraints(List<Constraint> constraints) {
            rule.constraints.addAll(constraints);
            return (B) this;
        }

    }

}
