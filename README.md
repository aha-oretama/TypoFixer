[![CircleCI](https://circleci.com/gh/aha-oretama/TypoFixer.svg?style=svg)](https://circleci.com/gh/aha-oretama/TypoFixer)

# TypoFixer

## Overview

In a pull request, *TypoFixer* points out your typos instead of reviewers, and fixes typos by just selecting the suggestions!

1. When you create or update a pull request, *TypoFixer* checks it and adds review comments and suggestions of your typos if there are typos.
2. If there is a suitable word in the suggestions, you select it. Then *TypoFixer* fixes your typo and updates your pull request.
3. If the pointed word is not a typo, you select `Not Typo`. Then *TypoFixer* registers the word in a dictionary and never point out the word as typo.

## Install

You can install by only authorizing *TypoFixer* from [install page](https://github.com/apps/typofixer).

## Configuration

If you want to customize *TypoFixer*, you create a configuration file named *typo-fixer.json* just under root of your repository.

```typo-fixer.json
{
   "extensions": [".md"]
}
```

| key | explanation |
| --- | ----------- |
| extensions | file extensions's array that you want to review, default is reviewing all files |
