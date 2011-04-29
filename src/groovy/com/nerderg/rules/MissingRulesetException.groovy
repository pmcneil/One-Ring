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

/**
 * User: pmcneil
 * Date: 28/01/11
 *
 */
class MissingRulesetException extends Exception {

    MissingRulesetException() {
    }

    MissingRulesetException(String message) {
        super(message)
    }

    MissingRulesetException(String message, Throwable cause) {
        super(message, cause)
    }

    MissingRulesetException(Throwable cause) {
        super(cause)
    }
}
