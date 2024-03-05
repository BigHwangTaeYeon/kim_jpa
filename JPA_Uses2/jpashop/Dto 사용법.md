# api와 화면은 패키지를 분리한다.
공통 처리와 화면을 분리.

# MemberDto
/api/v1/members 를 보면 반환타입이 List<Member>이다
```json
[
    {
        "id": 2,
        "name": "newHello",
        "address": {
            "city": "인천",
            "street": "검암",
            "zipcode": "1001"
        },
        "orders": []
    },
    {
        "id": 3,
        "name": "name",
        "address": {
            "city": "부산",
            "street": "광안리",
            "zipcode": "593"
        },
        "orders": []
    }
]
```
[] -> array
json 배열타입으로 들어가 유연성이 떨어진다.

만약 count 를 넣어달라는 요청이 들어오면 해결할 수가 없다 
JSON 스펙이 깨져버린다.
```json
[
    "count" : 4
    , {
        "" : ""
    }
    , ...
]
```

/api/v2/members 에서 
```java
@GetMapping("/api/v2/members")
public Result memberV2() {
    List<Member> findMembers = memberService.findMembers();
    List<MemberDto> collect = findMembers.stream()
            .map(m -> new MemberDto(m.getName()))
            .collect(Collectors.toList());
    return new Result(collect);
}

@Data
@AllArgsConstructor
static class Result<T> {
    private T data;
}

@Data
@AllArgsConstructor
static class MemberDto {
    private String name;
}
```

```json
{
    "data": [
        {
            "name": "newHello"
        },
        {
            "name": "newHello"
        }
    ]
}
```
이런식으로 감싸주지 않으면 json 배열타입으로 들어가 유연성이 떨어진다.

이제는 추가 요청에 해결이 가능하다
```java
@GetMapping("/api/v2/members")
public Result memberV2() {
    List<Member> findMembers = memberService.findMembers();
    List<MemberDto> collect = findMembers.stream()
            .map(m -> new MemberDto(m.getName()))
            .collect(Collectors.toList());
    return new Result(collect.size(), collect);
}

@Data
@AllArgsConstructor
static class Result<T> {
    private int count;
    private T data;
}
```

```json
{
    "count" : 4,
    "data": [
        {
            "name": "newHello"
        },
        {
            "name": "newHello"
        }
    ]
}
```