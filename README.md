# Baedal-Mate
BaedalMate Application for Android <br />

## Installation
Clone this repository and import into **Android Studio**
```bash
git clone git@github.com:BaedalMate/BaedalMate_Android.git
```

## Development Environment
- Android Studio Bumblebee | 2021.1.1 Patch 2<br>
- Kotlin 1.5.30

## Application Version
- minSdkVersion : 28<br>
- targetSdkVersion : 32

## Git Convention

- Create issue<br>
- Create branch for each issue (feature/issue-#) -> eg) feature/issue-1<br>
- After develop merged, Delete branch<br>

## Commit Convention

- [feat] : Add new feature<br>
- [layout] : Add and modify layout and xml<br>
- [bug] : Error fix<br>
- [doc] : Add and modify documents
- [refactor] : code Refactoring
- [style] : NO CODE CHANGE. code formatting, convert tabs to spaces, etc
- [chore] : NO CODE CHANGE. updating grunt tasks, etc
- [test]  : Add test code
- <b>commit message rule</b>
    - Don't use a period
    - Write a command without using the past tense.
      example) "Fixed" -> "Fix", "Added" -> "Add"
- commit message Example : #issue number {issue title} e.g. #1 [layout] MainActivity<br>

## Xml Naming Convention

- Basic Principle : (what) _ (where) _ (description) _ (size)
- layout : (what) _ (where).xml
  e.g. activity_main.xml
- drawables : (what) _ (description) _ (size)
  e.g. ic_notify_icon_24
- ids : (what)_ (where) _ (description)
  e.g. tv_main_nickname
- strings : (where) _ (description)
- color : (description) _ (colorCode)
  e.g. white_FFFFFF<br>

- textView → tv / EditText → et / RecyclerView → rv / ImageView → img / Button → btn<br>
- state list drawable → selector_(where) _ (what)
  e.g. selector_main_button
- shape drawable → border _(color) _ ([fill/line/fill_line]) _ (radius)
  e.g. border_white_fill_12