/* Copyright 2010, 2011 Peter McNeil

This file is part of One Ring.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy
of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package com.nerderg.rules

import grails.test.ControllerUnitTestCase

class RuleSetControllerTests extends ControllerUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testLimit() {
        assert controller.limit([], 10, 0) == []
        assert controller.limit([1,2,3,4,5], 10, 0) == [1,2,3,4,5]
        assert controller.limit([1,2,3,4,5], 10, 5) == []
        assert controller.limit([1,2,3,4,5], 10, 6) == []
        assert controller.limit([1,2,3,4,5], 10, 60) == []
        assert controller.limit([1,2,3,4,5], 10, 4) == [5]
        assert controller.limit([1,2,3,4,5], 10, 1) == [2,3,4,5]
        assert controller.limit([1,2,3,4,5,6,7,8,9,10,11,12,13,14,15], 10, 0) == [1,2,3,4,5,6,7,8,9,10]
        assert controller.limit([1,2,3,4,5,6,7,8,9,10,11,12,13,14,15], 10, 10) == [11,12,13,14,15]
        assert controller.limit([1,2,3,4,5,6,7,8,9,10,11,12,13,14,15], 10, 20) == []
        assert controller.limit([1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23], 10, 10) == [11,12,13,14,15,16,17,18,19,20]
    }
}
