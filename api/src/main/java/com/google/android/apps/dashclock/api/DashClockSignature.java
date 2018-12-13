/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.apps.dashclock.api;

import android.content.pm.Signature;

/**
 * The signature of the official DashClock app (net.nurik.roman.dashclock).
 */
public final class DashClockSignature {
    /**
     * The signature of the official DashClock app (net.nurik.roman.dashclock).
     */
    public static final Signature SIGNATURE = new Signature(""
            + "308203523082023aa00302010202044c1132a9300d06092a864886f70d0101050500306b310b30090603"
            + "550406130255533110300e06035504081307556e6b6e6f776e3110300e06035504071307556e6b6e6f77"
            + "6e3110300e060355040a1307556e6b6e6f776e3110300e060355040b1307556e6b6e6f776e3114301206"
            + "03550403130b526f6d616e204e7572696b301e170d3130303631303138343435375a170d333731303236"
            + "3138343435375a306b310b30090603550406130255533110300e06035504081307556e6b6e6f776e3110"
            + "300e06035504071307556e6b6e6f776e3110300e060355040a1307556e6b6e6f776e3110300e06035504"
            + "0b1307556e6b6e6f776e311430120603550403130b526f6d616e204e7572696b30820122300d06092a86"
            + "4886f70d01010105000382010f003082010a02820101008906222723a4b30dca6f0702b041e6f361e38e"
            + "35105ec530bf43f4f1786737fefe6ccfa3b038a3700ea685dd185112a0a8f96327d3373de28e05859a87"
            + "bde82372baed5618082121d6946e4affbdfb6771abb782147d58a2323518b34efcce144ec3e45fb2556e"
            + "ba1c40b42ccbcc1266c9469b5447edf09d5cf8e2ed62cfb3bd902e47f48a11a815a635c3879c882eae92"
            + "3c7f73bfba4039b7c19930617e3326fa163b924eda398bacc0d6ef8643a32223ce1d767734e866553ad5"
            + "0d11fb22ac3a15ba021a6a3904a95ed65f54142256cb0db90038dd55adfeeb18d3ffb085c4380817268f"
            + "039119ecbdfca843e4b82209947fd88470b3d8c76fc15878fbc4f10203010001300d06092a864886f70d"
            + "0101050500038201010047063efdd5011adb69cca6461a57443fef59243f85e5727ec0d67513bb04b650"
            + "b1144fc1f54e09789c278171c52b9305a7265cafc13b89d91eb37ddce34a5c1f17c8c36f86c957c4e9ca"
            + "cc19e6822e0a5711f2cfba2c5913ba582ab69485548b13072bc736310b9da85a716d0418e6449450ceda"
            + "dfc1c897f93ed6189cfa0a02b893125bd4b1c4e4dd50c1ad33e221120b8488841763a3361817081e7691"
            + "1e76d3adcf94b23c758ceb955f9fdf8ef4a8351fc279867a25729f081b511209e96dfa8520225b810072"
            + "de5e8eefc1a6cc22f46857e2cc4fd1a1eaac76054f34352b63c9d53691515b42cc771f195343e61397cb"
            + "7b04ada2a627410d29c214976d13");

    /**
     * The signature of the Chronus DashClock Host app (com.dvtonder.extensionhost).
     */
    /*
    public static final Signature CHRONUS_SIGNATURE = new Signature(""
            + "308203433082022ba0030201020204065f73ff300d06092a864886f70d01010b05003052310b30090603"
            + "550406130243413110300e060355040813074f6e746172696f3110300e06035504071307546f726f6e74"
            + "6f311f301d060355040313164461766964204d616c616e2076616e20546f6e646572301e170d31333039"
            + "32333233333530345a170d3338303931373233333530345a3052310b3009060355040613024341311030"
            + "0e060355040813074f6e746172696f3110300e06035504071307546f726f6e746f311f301d0603550403"
            + "13164461766964204d616c616e2076616e20546f6e64657230820122300d06092a864886f70d01010105"
            + "000382010f003082010a0282010100df477b48834253fa43a1b9ef0b28e9d931ef60a8ac4cbaeae07598"
            + "134c385943675498e951d2218d7377f7ec120103ad9717ab8e07aaa3ddd7f805a24cb505bbe25f875ba4"
            + "671e857cacf02c295ad20c70812648e2f62c3546c09bff4a3ac2b40465aa191cbdaea96168ccfb88c2b5"
            + "d5d8300067fe1d7cd25775bbf8964884ebac08df5d42afff6474909eb30bc7ee1531e3c6e9f73e0cf163"
            + "d4e9b2cc61f80436fbc5ffd1c63cc3082e718c7ed8145d9c2b90ad7f1cd8fa320604a16d8d9a2ee066b8"
            + "99bec221d867599410c358e0b077a186130ad98d59ba619fa83cc40391b4d19d8c012bc3a0d829f320a3"
            + "e666936b472e0cc630a8ac2e422114a6906cc30203010001a321301f301d0603551d0e04160414f44b40"
            + "bca284c99c659b76cbfd78300493f24f80300d06092a864886f70d01010b0500038201010087a170dda9"
            + "a6510d1a0ac1b05a8bd9a36d59a5f5e0695c0abc6f31b45f287929f25d7fe99ed08efdda6e783aea6913"
            + "0b448822760ad7120662491c5c70660ad627ad98b4866e2ff16830cc4454518affec668638b8cc1007e2"
            + "57e4141cf0398dc320eab5baa73b534d5d2874a56fa4f898f00ddf9df3673ceac1dea6a6784c28ea73eb"
            + "772b21a631c7cb54936954a656bf78e5dfa9d2ba5a587b30f9d4743203effd6c25112e62d17da4d1b781"
            + "25dcdc12a81a17529a569717d8916d8ff83437d54b2a20ff57c51141ef4e0bb4b65f14bcf827fc981fd3"
            + "4b5703df3580293a0762582965b5403ac57d41d083e4d235a3859753c59336309a0a130f0e669406c");
            */
}
