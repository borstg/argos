package com.rabobank.argos.service.adapter.in.rest.layout;

import com.rabobank.argos.domain.layout.rule.AllowRule;
import com.rabobank.argos.domain.layout.rule.CreateRule;
import com.rabobank.argos.domain.layout.rule.DeleteRule;
import com.rabobank.argos.domain.layout.rule.DisallowRule;
import com.rabobank.argos.domain.layout.rule.MatchRule;
import com.rabobank.argos.domain.layout.rule.ModifyRule;
import com.rabobank.argos.domain.layout.rule.RequireRule;
import com.rabobank.argos.domain.layout.rule.Rule;
import com.rabobank.argos.service.adapter.in.rest.api.model.RestRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


@Mapper(componentModel = "spring")
public abstract class RuleMapper {

    @Autowired
    private MatchRuleMapper matchRuleMapper;

    private static final Map<Class<? extends Rule>, RestRule.RuleTypeEnum> RULE_TYPE_MAP = new HashMap<>();

    static {
        RULE_TYPE_MAP.put(AllowRule.class, RestRule.RuleTypeEnum.ALLOW);
        RULE_TYPE_MAP.put(CreateRule.class, RestRule.RuleTypeEnum.CREATE);
        RULE_TYPE_MAP.put(DeleteRule.class, RestRule.RuleTypeEnum.DELETE);
        RULE_TYPE_MAP.put(DisallowRule.class, RestRule.RuleTypeEnum.DISALLOW);
        RULE_TYPE_MAP.put(MatchRule.class, RestRule.RuleTypeEnum.MATCH);
        RULE_TYPE_MAP.put(ModifyRule.class, RestRule.RuleTypeEnum.MODIFY);
        RULE_TYPE_MAP.put(RequireRule.class, RestRule.RuleTypeEnum.REQUIRE);
    }

    @ObjectFactory
    public Rule createRule(RestRule restRule) {
        switch (restRule.getRuleType()) {
            case ALLOW:
                return AllowRule.builder().build();
            case CREATE:
                return CreateRule.builder().build();
            case DELETE:
                return DeleteRule.builder().build();
            case DISALLOW:
                return DisallowRule.builder().build();
            case MATCH:
                return matchRuleMapper.mapFromRestRule(restRule);
            case MODIFY:
                return ModifyRule.builder().build();
            case REQUIRE:
                return RequireRule.builder().build();
        }
        return null;
    }

    @ObjectFactory
    public RestRule createRule(Rule rule) {
        if (rule instanceof MatchRule) {
            return matchRuleMapper.mapToRestRule((MatchRule) rule);
        }
        return new RestRule();
    }

    public abstract Rule mapFromRestRule(RestRule restRule);

    @Mapping(source = "rule", target = "ruleType", qualifiedByName = "typeFromRule")
    public abstract RestRule mapFromRestRule(Rule rule);

    @Named("typeFromRule")
    public RestRule.RuleTypeEnum typeFromRule(Rule rule) {
        return RULE_TYPE_MAP.get(rule.getClass());
    }

}

