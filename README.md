# Spring Filter

<p align="center">
  <a href="https://github.com/turkraft/spring-filter">
    <img src="https://i.imgur.com/0otEurv.png" alt="Spring Filter Logo" width="180">
  </a>
</p>

You need a way to dynamically filter entities without any effort? Just add me to your `pom.xml`.
Your API will gain a full featured search functionality. You don't work with APIs? No problem, you may still not want to mess with SQL, JPA predicates, security, and all of that I guess. From a technical point of view, I compile a simple syntax to JPA predicates.

## Example ([try it live](https://spring-filter.herokuapp.com))
*/search?filter=* **average**(ratings) **>** 4.5 **and** brand.name **in** ('audi', 'land rover') **and** (year **>** 2018 **or** km **<** 50000) and color **:** 'white' **and** accidents **is empty**

```java
/* Entity used in the query above */
@Entity public class Car {
  @Id long id;
      int year;
      int km;
  @Enumerated Color color;
  @ManyToOne Brand brand;
  @OneToMany List<Accident> accidents;
  @ElementCollection List<Integer> ratings;
  // ...
}
```

> :rocket: Yes we support booleans, dates, enums, functions, **and even relations**! Need something else? [Tell us here](https://github.com/torshid/spring-filter/issues).

## Installation

```xml
<dependency>
    <groupId>com.turkraft</groupId>
    <artifactId>spring-filter</artifactId>
    <version>1.0.5</version>
</dependency>
```

## Usages

### a. Controller
> Requires **javax.persistence-api**, **spring-data-jpa**, **spring-web** and **spring-webmvc**
```java
@GetMapping(value = "/search")
public List<Entity> search(@Filter Specification<Entity> spec, Pageable page) {
  return repo.findAll(spec, page);
}
```
> The repository should implement `JpaSpecificationExecutor` in order to execute Spring's Specification, `SimpleJpaRepository` is a well known implementation. You can remove the `Pageable` argument if pagination is not needed.
> 
### b. Specification
> Requires **javax.persistence-api**, **spring-data-jpa**, **spring-web**
```java
Specification<Entity> spec = new FilterSpecification<Entity>(query);
```

### c. Predicate
> Requires **javax.persistence-api**
```java
Predicate predicate = ExpressionGenerator.run(String query, Root<?> r, CriteriaQuery<?> q, CriteriaBuilder cb);
```

> :warning: **If you need to search over relations**, you also require **hibernate-core**

### d. Builder
```java
/* Using static methods */
import static com.turkraft.springfilter.FilterBuilder.*;
Filter filter = filter(like("name", "%jose%"));
String query = filter.generate(); // name ~ '%jose%'
// Predicate predicate = ExpressionGenerator.run(filter, Root<?> r, CriteriaQuery<?> cq, CriteriaBuilder cb);
// Specification<Entity> spec = new FilterSpecification<Entity>(filter);
```

## Syntax

### Fields
Field names should be directly given without any extra literals. Dots indicate nested fields. For example: `category.updatedAt`

### Inputs
Numbers should be directly given. Booleans should also directly be given, valid values are `true` and `false` (case insensitive). Others such as strings, enums, dates, should be quoted. For example: `status : 'active'`

### Operators
<table>
  <tr> <th>Literal (case insensitive)</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td>and</th> <td>and's two expressions</td> <td>status : 'active' <b>and</b> createdAt > '1-1-2000'</td> </tr>
  <tr> <td>or</th> <td>or's two expressions</td> <td>value ~ '%hello%' <b>or</b> name ~ '%world%'</td> </tr>
  <tr> <td>not</th> <td>not's an expression</td> <td> <b>not</b> (id > 100 or category.order is null) </td> </tr>
</table>

> You may prioritize operators using parentheses, for example: `x and (y or z)`

### Comparators
<table>
  <tr> <th>Literal (case insensitive)</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td>~</th> <td>checks if the left (string) expression is similar to the right (string) expression</td> <td>catalog.name <b>~</b> 'electronic%'</td> </tr>
  <tr> <td>:</th> <td>checks if the left expression is equal to the right expression</td> <td>id <b>:</b> 5</td> </tr>
  <tr> <td>!</th> <td>checks if the left expression is not equal to the right expression</td> <td>username <b>!</b> 'torshid'</td> </tr>
  <tr> <td>></th> <td>checks if the left expression is greater than the right expression</td> <td>distance <b>></b> 100</td> </tr>
  <tr> <td>>:</th> <td>checks if the left expression is greater or equal to the right expression</td> <td>distance <b>>:</b> 100</td> </tr>
  <tr> <td><</th> <td>checks if the left expression is smaller than the right expression</td> <td>distance <b><</b> 100</td> </tr>
  <tr> <td><:</th> <td>checks if the left expression is smaller or equal to the right expression</td> <td>distance <b><:</b> 100</td> </tr>
  <tr> <td>is null</th> <td>checks if an expression is null</td> <td>status <b>is null</b></td> </tr>
  <tr> <td>is not null</th> <td>checks if an expression is not null</td> <td>status <b>is not null</b></td> </tr>
  <tr> <td>is empty</th> <td>checks if the (collection) expression is empty</td> <td>children <b>is empty</b></td> </tr>
  <tr> <td>is not empty</th> <td>checks if the (collection) expression is not empty</td> <td>children <b>is not empty</b></td> </tr>
  <tr> <td>in</th> <td>checks if an expression is present in the right expressions</td> <td>status <b>in (</b>'initialized'<b>,</b> 'active'<b>)</b></td> </tr>
</table>

> Note that the `*` character can also be used instead of `%` when using the `~` comparator. By default, this comparator is case insensitive, the behavior can be changed with `ExpressionGeneratorParameters.CASE_SENSITIVE_LIKE_OPERATOR`.

### Functions
A function is characterized by its name (case insensitive) followed by parentheses. For example: `currentTime()`. Some functions might also take arguments, arguments are seperated with commas. For example: `min(ratings) > 3`
<table>
  <tr> <th>Name</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td> absolute </th> <td> returns the absolute </td> <td> <b>absolute(</b>x<b>)</b> </td> </tr>
  <tr> <td> average </th> <td> returns the average </td> <td> <b>average(</b>ratings<b>)</b> </td> </tr>
  <tr> <td> min </th> <td> returns the minimum </td> <td> <b>min(</b>ratings<b>)</b> </td> </tr>
  <tr> <td> max </th> <td> returns the maximum </td> <td> <b>max(</b>ratings<b>)</b> </td> </tr>
  <tr> <td> sum </th> <td> returns the sum </td> <td> <b>sum(</b>scores<b>)</b> </td> </tr>
  <tr> <td> currentDate </th> <td> returns the current date </td> <td> <b>currentDate()</b> </td> </tr>
  <tr> <td> currentTime </th> <td> returns the current time </td> <td> <b>currentTime()</b> </td> </tr>
  <tr> <td> currentTimestamp </th> <td> returns the current time stamp </td> <td> <b>currentTimestamp()</b> </td> </tr>
  <tr> <td> size </th> <td> returns the collection's size </td> <td> <b>size(</b>accidents<b>)</b> </td> </tr>
  <tr> <td> length </th> <td> returns the string's length </td> <td> <b>length(</b>name<b>)</b> </td> </tr>
  <tr> <td> trim </th> <td> returns the trimmed string </td> <td> <b>trim(</b>name<b>)</b> </td> </tr>
  <tr> <td> upper </th> <td> returns the uppercased string </td> <td> <b>upper(</b>name<b>)</b> </td> </tr>
  <tr> <td> lower </th> <td> returns the lowercased string </td> <td> <b>lower(</b>name<b>)</b> </td> </tr>
  <tr> <td> concat </th> <td> returns the concatenation of given strings </td> <td> <b>concat(</b>firstName<b>,</b> ' '<b>,</b> lastName<b>)</b> </td> </tr>
</table>

## Configuration
You may want to customize the behavior of the different processes taking place. For now, you can only change the date format but advanced customization will be soon available in order to let you completely personalize the tokenizer, the parser, the query builder, with the possibility of adding custom functions and much more.

### Date format
You are able to change the date format by setting the static formatters inside the `SpringFilterParameters` class. You may see below the default patterns and how you can set them with properties:

<table>
  <tr> <th>Type</th> <th>Default Pattern</th> <th>Property Name</th> </tr>
  <tr> <td> java.util.Date </th> <td> dd-MM-yyyy </td> <td> turkraft.springfilter.dateformatter.pattern </td> </tr>
  <tr> <td> java.time.LocalDate </th> <td> dd-MM-yyyy </td> <td> turkraft.springfilter.localdateformatter.pattern </td> </tr>
  <tr> <td> java.time.LocalDateTime </th> <td> dd-MM-yyyy'T'HH:mm:ss </td> <td> turkraft.springfilter.localdatetimeformatter.pattern </td> </tr>
  <tr> <td> java.time.OffsetDateTime </th> <td> dd-MM-yyyy'T'HH:mm:ss.SSSXXX </td> <td> turkraft.springfilter.offsetdatetimeformatter.pattern </td> </tr>
  <tr> <td> java.time.Instant </th> <td> dd-MM-yyyy'T'HH:mm:ss.SSSXXX </td> <td> <i>Parses using DateTimeFormatter.ISO_INSTANT</i> </td> </tr>
</table>

## MongoDB
MongoDB is also partially supported as an alternative to JPA. The query input is compiled to a `Bson`/`Document` filter. You can then use it as you wish with `MongoTemplate` or `MongoOperations` for example. 

> Requires **spring-data-mongodb** 

### Usage
```java
@GetMapping(value = "/search")
public List<Entity> search(@Filter(entityClass = Entity.class) Document doc, Pageable page) {
  // your repo may implement DocumentExecutor for easy usage
  return repo.findAll(doc, page); 
}
```
```java
Bson bson = BsonGenerator.run(Entity.class, filter);
Document doc = BsonUtils.getDocumentFromBson(bson);
Query query = BsonUtils.getQueryFromDocument(doc);
// ...
```

> :warning: Functions are currently not supported with MongoDB

## Articles
* [Easily filter entities in your Spring API](https://torshid.medium.com/easily-filter-entities-in-your-spring-api-f433537cfd41)

## Contributing
Ideas and pull requests are always welcome. [Google's Java Style](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml) is used for formatting.

## Contributors
Thanks to [@marcopag90](https://github.com/marcopag90) and [@glodepa](https://github.com/glodepa) for adding support to MongoDB.

## License
Distributed under the [MIT license](LICENSE).
