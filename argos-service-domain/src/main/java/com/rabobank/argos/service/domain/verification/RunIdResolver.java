/*
 * Copyright (C) 2019 Rabobank Nederland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rabobank.argos.service.domain.verification;

import com.rabobank.argos.domain.layout.Layout;
import com.rabobank.argos.domain.layout.LayoutMetaBlock;
import com.rabobank.argos.domain.layout.MatchFilter;
import com.rabobank.argos.domain.link.Artifact;
import com.rabobank.argos.domain.link.Link;
import com.rabobank.argos.domain.link.LinkMetaBlock;
import com.rabobank.argos.service.domain.link.LinkMetaBlockRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.rabobank.argos.domain.layout.DestinationType.MATERIALS;
import static com.rabobank.argos.domain.layout.DestinationType.PRODUCTS;
import static java.util.stream.Collectors.toList;

@Component
@Slf4j
@RequiredArgsConstructor
public class RunIdResolver {

    private final LinkMetaBlockRepository linkMetaBlockRepository;

    @Getter
    @Builder
    @ToString
    private static class MatchedProductWithRunIds {
        private final String supplyChainId;
        private final MatchFilter matchFilter;
        private List<Artifact> matchedProductsToVerify;
        @Builder.Default
        private Set<String> runIds = new TreeSet<>();

        String getStepName() {
            return matchFilter.getDestinationStepName();
        }

        List<String> getHashes() {
            return matchedProductsToVerify.stream().map(Artifact::getHash).collect(toList());
        }

    }

    public Optional<String> getRunId(LayoutMetaBlock layoutMetaBlock, List<Artifact> productsToVerify) {

        Layout layout = layoutMetaBlock.getLayout();
        List<MatchedProductWithRunIds> matchedProductWithRunIds = layout
                .getExpectedEndProducts()
                .stream()
                .map(expectedEndProduct -> MatchedProductWithRunIds.builder()
                        .supplyChainId(layoutMetaBlock.getSupplyChainId())
                        .matchFilter(expectedEndProduct)
                        .matchedProductsToVerify(expectedEndProduct.matches(productsToVerify))
                        .build())
                .peek(this::addRunIds)
                .peek(p -> log.info("{}", p))
                .collect(toList());


        Set<String> allRunIds = matchedProductWithRunIds
                .stream()
                .map(MatchedProductWithRunIds::getRunIds)
                .flatMap(Set::stream).collect(Collectors.toCollection(TreeSet::new));
        return allRunIds.stream().filter(runId -> isInAll(runId, matchedProductWithRunIds)).findFirst();
    }

    private boolean isInAll(String runId, List<MatchedProductWithRunIds> matchedProductWithRunIdsList) {
        return matchedProductWithRunIdsList.stream().allMatch(matchedProductWithRunIds -> matchedProductWithRunIds.getRunIds().contains(runId));
    }

    private void addRunIds(MatchedProductWithRunIds matchedProductWithRunIds) {
        if (PRODUCTS == matchedProductWithRunIds.matchFilter.getDestinationType()) {
            searchInProductHashes(matchedProductWithRunIds);

        } else if (MATERIALS == matchedProductWithRunIds.matchFilter.getDestinationType()) {
            searchInMaterialsHashes(matchedProductWithRunIds);
        }
    }

    private void searchInMaterialsHashes(MatchedProductWithRunIds matchedProductWithRunIds) {
        matchedProductWithRunIds.getRunIds().addAll(
                linkMetaBlockRepository
                        .findBySupplyChainAndStepNameAndMaterialHash(
                                matchedProductWithRunIds.getSupplyChainId(),
                                matchedProductWithRunIds.getStepName(),
                                matchedProductWithRunIds.getHashes())
                        .stream()
                        .map(LinkMetaBlock::getLink)
                        .map(Link::getRunId).collect(toList()));
    }

    private void searchInProductHashes(MatchedProductWithRunIds matchedProductWithRunIds) {
        matchedProductWithRunIds.getRunIds().addAll(
                linkMetaBlockRepository
                        .findBySupplyChainAndStepNameAndProductHashes(
                                matchedProductWithRunIds.getSupplyChainId(),
                                matchedProductWithRunIds.getStepName(),
                                matchedProductWithRunIds.getHashes())
                        .stream()
                        .map(LinkMetaBlock::getLink)
                        .map(Link::getRunId).collect(toList()));
    }


}
