![배달메이트_로고디자인_최종](https://user-images.githubusercontent.com/65344669/224562308-380293c9-4226-49b0-9da1-87d01b173e10.png)
<p align='center'>
  <a href="https://github.com/BaedalMate/BaedalMate_Android/issues"><img src='https://img.shields.io/github/issues/BaedalMate/BaedalMate_Android'></a>
 <a href="https://github.com/BaedalMate/BaedalMate_Android/graphs/contributors"><img src='https://img.shields.io/github/contributors/BaedalMate/BaedalMate_Android'></a>
</p>
# 함께 주문하고 나눠요, 배달메이트🍴
> 함께 배달 시킬사람을 모집하는 단체주문 모집 커뮤니티 서비스

## Key Function
- 모집 게시글 업로드
   - 함께 주문하고 싶은 음식을 선정한 뒤, 모집을 언제까지 할 것인지 마감 기준을 선택한 뒤 함꼐 배달 시킬 사람을 모집합니다
- 모집 게시글 참가
   - 참가하고 싶은 모집글을 조회해서, 본인이 시킬 메뉴를 입력한 뒤 모집글에 참가합니다.
   - 메인 페이지 상단엔 내 위치에서 모집하고 있는 게시글이 우선적으로 표시됩니다.
- 모집 인원간 실시간 채팅
   - 모집 글에 참가한 경우 모집 인원간 어떻게 시키고 어디서 모일것인지 상세하게 의견을 주고받기 위해 실시간 채팅 기능을 제공합니다
   - 해당 채팅방에서 현재 모집의 상태, 모집글에 참여한 사람들의 주문 내역, 총 주문 금액 등을 확인할 수 있습니다
- 상호 리뷰
   - 모든 배달과 나눔이 끝나고 자신을 제외한 참가한 사람들에 대한 리뷰가 가능합니다
   - 해당 별점 리뷰는 모집글을 올리는 사람의 별점에 영향을 미쳐, 해당 사용자에 대한 신뢰성을 확인하는 용도로 사용될 수 있습니다

## Language and Architecture
- Kotlin
- Clean Architecture With MVVM Pattern

## Team Members for Android
- 허동준(<a href="https://github.com/DongJun-H">@DongJun-H</a>)

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
- targetSdkVersion : 33

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
