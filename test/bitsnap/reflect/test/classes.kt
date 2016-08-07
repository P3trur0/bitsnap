/**
 *  Copyright 2016 Yuriy Yarosh
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 **/

package bitsnap.reflect.test

interface TestCompanionInterface {
    val testString : String
}

class AReflection {
    companion object : TestCompanionInterface {
        override val testString = "test"
    }
}

class BReflection {
    companion object : TestCompanionInterface {
        override val testString = "test"
    }
}

class CReflection {
    companion object : TestCompanionInterface {
        override val testString = "test"
    }
}
