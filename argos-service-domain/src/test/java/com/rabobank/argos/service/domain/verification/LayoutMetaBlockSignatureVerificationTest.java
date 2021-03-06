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

import com.rabobank.argos.domain.Signature;
import com.rabobank.argos.domain.key.KeyPair;
import com.rabobank.argos.domain.layout.Layout;
import com.rabobank.argos.domain.layout.LayoutMetaBlock;
import com.rabobank.argos.domain.signing.SignatureValidator;
import com.rabobank.argos.service.domain.key.KeyPairRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.PublicKey;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LayoutMetaBlockSignatureVerificationTest {

    private static final String KEY_ID = "keyId";
    private static final String SIG = "sig";
    @Mock
    private SignatureValidator signatureValidator;

    @Mock
    private KeyPairRepository keyPairRepository;

    @Mock
    private VerificationContext context;

    private LayoutMetaBlockSignatureVerification verification;

    @Mock
    private LayoutMetaBlock layoutMetaBlock;

    @Mock
    private Signature signature;

    @Mock
    private KeyPair keyPair;

    @Mock
    private PublicKey publicKey;

    @Mock
    private Layout layout;

    @BeforeEach
    void setUp() {
        verification = new LayoutMetaBlockSignatureVerification(signatureValidator, keyPairRepository);
    }

    @Test
    void getPriority() {
        assertThat(verification.getPriority(), is(Verification.Priority.LAYOUT_METABLOCK_SIGNATURE));
    }

    @Test
    void verifyOkay() {
        mockSetup(true);
        assertThat(verification.verify(context).isRunIsValid(), is(true));
    }

    @Test
    void verifyNotOkay() {
        mockSetup(false);
        assertThat(verification.verify(context).isRunIsValid(), is(false));
    }

    private void mockSetup(boolean valid) {
        when(layoutMetaBlock.getLayout()).thenReturn(layout);
        when(signatureValidator.isValid(layout, SIG, publicKey)).thenReturn(valid);
        when(signature.getSignature()).thenReturn(SIG);
        when(signature.getKeyId()).thenReturn(KEY_ID);
        when(keyPair.getPublicKey()).thenReturn(publicKey);
        when(keyPairRepository.findByKeyId(KEY_ID)).thenReturn(Optional.of(keyPair));
        when(context.getLayoutMetaBlock()).thenReturn(layoutMetaBlock);
        when(layoutMetaBlock.getSignatures()).thenReturn(Collections.singletonList(signature));
    }

    @Test
    void verifyKeyNotFound() {

        when(signature.getKeyId()).thenReturn(KEY_ID);
        when(keyPairRepository.findByKeyId(KEY_ID)).thenReturn(Optional.empty());
        when(context.getLayoutMetaBlock()).thenReturn(layoutMetaBlock);
        when(layoutMetaBlock.getSignatures()).thenReturn(Collections.singletonList(signature));
        assertThat(verification.verify(context).isRunIsValid(), is(false));
    }
}
