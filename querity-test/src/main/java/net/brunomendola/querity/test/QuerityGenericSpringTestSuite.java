package net.brunomendola.querity.test;

import net.brunomendola.querity.api.Querity;
import net.brunomendola.querity.api.Query;
import net.brunomendola.querity.test.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.brunomendola.querity.api.Operator.*;
import static net.brunomendola.querity.api.Querity.*;
import static net.brunomendola.querity.api.Sort.Direction.DESC;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class QuerityGenericSpringTestSuite<T extends Person<K, ?, ?, ? extends Order<? extends OrderItem>>, K extends Comparable<K>> {

  public static final String PROPERTY_ID = "id";
  public static final String PROPERTY_LAST_NAME = "lastName";
  public static final String PROPERTY_FIRST_NAME = "firstName";
  public static final String PROPERTY_BIRTH_DATE = "birthDate";
  public static final String PROPERTY_HEIGHT = "height";
  public static final String PROPERTY_CHILDREN = "children";
  public static final String PROPERTY_MARRIED = "married";
  public static final String PROPERTY_ADDRESS_CITY = "address.city";
  public static final String PROPERTY_VISITED_LOCATIONS_COUNTRY = "visitedLocations.country";
  public static final String PROPERTY_VISITED_LOCATIONS_CITIES = "visitedLocations.cities";
  public static final String PROPERTY_FAVOURITE_PRODUCT_CATEGORY = "favouriteProductCategory";

  @Autowired
  private DatabaseSeeder<T> databaseSeeder;

  private List<T> entities;
  private T entity1;
  private T entity2;

  @Autowired
  PersonRepository<T, K> repository;

  @Autowired
  Querity querity;

  @BeforeEach
  void setUp() {
    this.entities = databaseSeeder.getEntities();
    assertThat(this.entities).isNotEmpty();
    this.entity1 = getEntityFromList(20);
    this.entity2 = getEntityFromList(30);
  }

  @Nested
  class FilteringTests {

    @Test
    void givenNullQuery_whenFilterAll_thenReturnAllTheElements() {
      List<T> result = querity.findAll(getEntityClass(), null);
      assertThat(result).isNotEmpty();
      assertThat(result).hasSize(entities.size());
    }

    @Test
    void givenEmptyFilter_whenFilterAll_thenReturnAllTheElements() {
      Query query = Querity.query()
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities);
    }

    @Test
    void findIssue() {
      Query query = Querity.query()
              .filter(filterBy("taaaac", EQUALS, "ops"))
              .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result.get(0).getTaaaac()).isEqualTo("ops");
    }

    @Test
    void givenFilterWithStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> entity1.getLastName().equals(p.getLastName()))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithIntegerEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_CHILDREN, EQUALS, entity1.getChildren()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getChildren() != null && p.getChildren().equals(entity1.getChildren()))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithIntegerAsStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_CHILDREN, EQUALS, entity1.getChildren()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getChildren() != null && p.getChildren().equals(entity1.getChildren()))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithDateEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_BIRTH_DATE, EQUALS, entity1.getBirthDate()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getBirthDate() != null && p.getBirthDate().isEqual(entity1.getBirthDate()))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithDateAsStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_BIRTH_DATE, EQUALS, formatDate(entity1.getBirthDate())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getBirthDate() != null && p.getBirthDate().isEqual(entity1.getBirthDate()))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithBooleanEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_MARRIED, EQUALS, true))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(Person::isMarried)
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithBooleanAsStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_MARRIED, EQUALS, Boolean.TRUE.toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(Person::isMarried)
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithIntegerEqualsConditionAsString_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_CHILDREN, EQUALS, entity1.getChildren().toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getChildren() != null && p.getChildren().equals(entity1.getChildren()))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithBigDecimalEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_HEIGHT, EQUALS, entity1.getHeight()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getHeight().compareTo(entity1.getHeight()) == 0)
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithBigDecimalEqualsConditionAsString_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_HEIGHT, EQUALS, entity1.getHeight().toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getHeight().compareTo(entity1.getHeight()) == 0)
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithBigDecimalGreaterThanCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_HEIGHT, GREATER_THAN, entity1.getHeight().toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getHeight().compareTo(entity1.getHeight()) > 0)
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithNotBigDecimalGreaterThanCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_HEIGHT, GREATER_THAN, entity1.getHeight().toString())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> !(p.getHeight().compareTo(entity1.getHeight()) > 0))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithBigDecimalGreaterThanEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_HEIGHT, GREATER_THAN_EQUALS, entity1.getHeight().toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getHeight().compareTo(entity1.getHeight()) >= 0)
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithNotBigDecimalGreaterThanEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_HEIGHT, GREATER_THAN_EQUALS, entity1.getHeight().toString())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> !(p.getHeight().compareTo(entity1.getHeight()) >= 0))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithBigDecimalLesserThanCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_HEIGHT, LESSER_THAN, entity1.getHeight().toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getHeight().compareTo(entity1.getHeight()) < 0)
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithNotBigDecimalLesserThanCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_HEIGHT, LESSER_THAN, entity1.getHeight().toString())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> !(p.getHeight().compareTo(entity1.getHeight()) < 0))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithBigDecimalLesserThanEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_HEIGHT, LESSER_THAN_EQUALS, entity1.getHeight().toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getHeight().compareTo(entity1.getHeight()) <= 0)
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithNotBigDecimalLesserThanEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_HEIGHT, LESSER_THAN_EQUALS, entity1.getHeight().toString())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> !(p.getHeight().compareTo(entity1.getHeight()) <= 0))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithStringNotEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, NOT_EQUALS, entity1.getLastName()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream().filter(p -> !entity1.getLastName().equals(p.getLastName())).collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithNotStringNotEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_LAST_NAME, NOT_EQUALS, entity1.getLastName())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> entity1.getLastName().equals(p.getLastName()))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithStringStartsWithCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      String prefix = entity1.getLastName().substring(0, 3);
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, STARTS_WITH, prefix))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getLastName() != null && p.getLastName().startsWith(prefix))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithStringEndsWithCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      String suffix = entity1.getLastName().substring(3);
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, ENDS_WITH, suffix))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getLastName() != null && p.getLastName().endsWith(suffix))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithStringContainsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      String substring = entity1.getLastName().substring(1, 3);
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, CONTAINS, substring))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getLastName() != null && p.getLastName().contains(substring))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithStringNotContainsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      String substring = entity1.getLastName().substring(1, 3);
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_LAST_NAME, CONTAINS, substring)))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getLastName() == null || !p.getLastName().contains(substring))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithStringIsNullCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, IS_NULL))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream().filter(p -> p.getLastName() == null).collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithStringIsNotNullCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, IS_NOT_NULL))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream().filter(p -> p.getLastName() != null).collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithNotStringIsNotNullCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_LAST_NAME, IS_NOT_NULL)))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream().filter(p -> p.getLastName() == null).collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithTwoStringEqualsConditionsWithAndLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(and(
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
              filterBy(PROPERTY_FIRST_NAME, EQUALS, entity1.getFirstName())
          ))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> entity1.getLastName().equals(p.getLastName()) && entity1.getFirstName().equals(p.getFirstName()))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithTwoStringEqualsConditionsWithOrLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(or(
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity2.getLastName())
          ))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> entity1.getLastName().equals(p.getLastName()) || entity2.getLastName().equals(p.getLastName()))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithNestedConditions_whenFindAll_thenReturnListOfEntity() {
      Query query = Querity.query()
          .filter(and(
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
              or(
                  filterBy(PROPERTY_FIRST_NAME, EQUALS, entity1.getFirstName()),
                  filterBy(PROPERTY_FIRST_NAME, EQUALS, entity2.getFirstName())
              ))
          )
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> entity1.getLastName().equals(p.getLastName()) &&
              (entity1.getFirstName().equals(p.getFirstName()) || entity2.getFirstName().equals(p.getFirstName())))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithStringEqualsConditionOnNestedField_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_ADDRESS_CITY, EQUALS, entity1.getAddress().getCity()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> entity1.getAddress().getCity().equals(p.getAddress().getCity()))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithStringEqualsConditionOnNestedCollectionItemField_whenFilterAll_thenReturnOnlyFilteredElements() {
      String visitedCountry = entity1.getVisitedLocations().get(0).getCountry();
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_VISITED_LOCATIONS_COUNTRY, EQUALS, visitedCountry))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getVisitedLocations().stream()
              .map(Location::getCountry)
              .anyMatch(visitedCountry::equals))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithStringEqualsConditionOnNestedCollectionItemStringListField_whenFilterAll_thenReturnOnlyFilteredElements() {
      String visitedCity = entity1.getVisitedLocations().get(0).getCities().get(0);
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_VISITED_LOCATIONS_CITIES, EQUALS, visitedCity))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getVisitedLocations().stream()
              .anyMatch(l -> l.getCities().contains(visitedCity)))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithTwoStringEqualsConditionOnNestedCollectionItemFieldsWithAndLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
      String visitedCountry = entity1.getVisitedLocations().get(0).getCountry();
      String visitedCity = entity1.getVisitedLocations().get(0).getCities().get(0);
      Query query = Querity.query()
          .filter(and(
              filterBy(PROPERTY_VISITED_LOCATIONS_COUNTRY, EQUALS, visitedCountry),
              filterBy(PROPERTY_VISITED_LOCATIONS_CITIES, EQUALS, visitedCity))
          )
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> p.getVisitedLocations().stream()
              .anyMatch(l -> visitedCountry.equals(l.getCountry()) &&
                  l.getCities().contains(visitedCity)))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithStringEqualsConditionOnDoubleNestedCollectionItemField_whenFilterAll_thenReturnOnlyFilteredElements() {
      String sku = entity1.getOrders().get(0).getItems().get(0).getSku();
      Query query = Querity.query()
          .filter(filterBy("orders.items.sku", EQUALS, sku))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(findByOrderContainingItemMatching(i -> i.getSku().equals(sku)));
    }

    @Test
    void givenFilterWithEnumEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      ProductCategory category = entity1.getFavouriteProductCategory();
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_FAVOURITE_PRODUCT_CATEGORY, EQUALS, category.name()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream().filter(e -> e.getFavouriteProductCategory().equals(category)).collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithNotConditionWithStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream().filter(p -> !(entity1.getLastName().equals(p.getLastName()))).collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithTwoNestedNotConditionsWithStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(not(filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()))))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream().filter(p -> entity1.getLastName().equals(p.getLastName())).collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithNotConditionWithTwoStringEqualsConditionsWithAndLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(and(
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
              filterBy(PROPERTY_FIRST_NAME, EQUALS, entity1.getFirstName())
          )))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> !(entity1.getLastName().equals(p.getLastName()) && entity1.getFirstName().equals(p.getFirstName())))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithNotConditionWithTwoStringEqualsConditionsWithOrLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(or(
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity2.getLastName())
          )))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> !(entity1.getLastName().equals(p.getLastName()) || entity2.getLastName().equals(p.getLastName())))
          .collect(Collectors.toList()));
    }

    @Test
    void givenFilterWithNotConditionWithNestedConditions_whenFindAll_thenReturnListOfEntity() {
      Query query = Querity.query()
          .filter(not(and(
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
              or(
                  filterBy(PROPERTY_FIRST_NAME, EQUALS, entity1.getFirstName()),
                  filterBy(PROPERTY_FIRST_NAME, EQUALS, entity2.getFirstName())
              ))
          ))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream()
          .filter(p -> !(entity1.getLastName().equals(p.getLastName()) && (entity1.getFirstName().equals(p.getFirstName()) || entity2.getFirstName().equals(p.getFirstName()))))
          .collect(Collectors.toList()));
    }

    private List<T> findByOrderContainingItemMatching(Predicate<OrderItem> matchPredicate) {
      return entities.stream()
          .filter(p -> p.getOrders().stream()
              .map(Order::getItems)
              .flatMap(Collection::stream)
              .anyMatch(matchPredicate))
          .collect(Collectors.toList());
    }

    private String formatDate(LocalDate birthDate) {
      return birthDate.format(DateTimeFormatter.ISO_DATE);
    }
  }

  @Nested
  class PaginationTests {

    @Test
    void givenPagination_whenFilterAll_thenReturnThePageElements() {
      Query query = Querity.query()
          .pagination(2, 3)
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream().skip(3).limit(3).collect(Collectors.toList()));
    }
  }

  @Nested
  class SortingTests {

    @Test
    void givenSortByFieldAscending_whenFilterAll_thenReturnSortedElements() {
      Query query = Querity.query()
          .sort(sortBy(PROPERTY_LAST_NAME), sortBy(PROPERTY_ID))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      Comparator<T> comparator = getStringComparator((T p) -> p.getLastName())
          .thenComparing((T p) -> p.getId());
      assertThat(result).isNotEmpty();
      assertThat(result).hasSize(entities.size());
      assertThat(result).isEqualTo(entities.stream().sorted(comparator).collect(Collectors.toList()));
    }

    @Test
    void givenSortByDateField_whenFilterAll_thenReturnSortedElements() {
      Query query = Querity.query()
          .sort(sortBy(PROPERTY_BIRTH_DATE), sortBy(PROPERTY_ID))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      Comparator<T> comparator = Comparator
          .comparing((T p) -> p.getBirthDate(), getSortComparator())
          .thenComparing((T p) -> p.getId());
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream().sorted(comparator).collect(Collectors.toList()));
    }

    @Test
    void givenSortByFieldDescending_whenFilterAll_thenReturnSortedElements() {
      Query query = Querity.query()
          .sort(sortBy(PROPERTY_LAST_NAME, DESC), sortBy(PROPERTY_ID))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      Comparator<T> comparator = getStringComparator((T p) -> p.getLastName()).reversed()
          .thenComparing((T p) -> p.getId());
      assertThat(result).isNotEmpty();
      assertThat(result).hasSize(entities.size());
      assertThat(result).isEqualTo(entities.stream().sorted(comparator).collect(Collectors.toList()));
    }

    @Test
    void givenSortByNestedField_whenFilterAll_thenReturnSortedElements() {
      Query query = Querity.query()
          .sort(sortBy(PROPERTY_ADDRESS_CITY), sortBy(PROPERTY_ID))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      Comparator<T> comparator = getStringComparator((T p) -> p.getAddress().getCity())
          .thenComparing((T p) -> p.getId());
      assertThat(result).isNotEmpty();
      assertThat(result).hasSize(entities.size());
      assertThat(result).isEqualTo(entities.stream().sorted(comparator).collect(Collectors.toList()));
    }

    @Test
    void givenSortByMultipleFields_whenFilterAll_thenReturnSortedElements() {
      Query query = Querity.query()
          .sort(sortBy(PROPERTY_LAST_NAME), sortBy(PROPERTY_FIRST_NAME))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      Comparator<T> comparator = getStringComparator((T p) -> p.getLastName())
          .thenComparing((T p) -> p.getFirstName());
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream().sorted(comparator).collect(Collectors.toList()));
    }
  }

  @Nested
  class CountTests {

    @Test
    void givenNullQuery_whenCount_thenReturnAllTheElementsCount() {
      Long count = querity.count(getEntityClass(), null);
      assertThat(count).isEqualTo(entities.size());
    }

    @Test
    void givenEmptyFilter_whenCount_thenReturnAllTheElementsCount() {
      Long count = querity.count(getEntityClass(), and());
      assertThat(count).isEqualTo(entities.size());
    }

    @Test
    void givenFilterWithStringEqualsCondition_whenCount_thenReturnOnlyFilteredElementsCount() {
      Long count = querity.count(getEntityClass(), filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()));
      assertThat(count).isEqualTo(entities.stream().filter(e -> entity1.getLastName().equals(e.getLastName())).count());
    }
  }

  protected abstract Class<T> getEntityClass();

  private T getEntityFromList(int skip) {
    return entities.stream()
        .filter(e -> e.getLastName() != null)
        .filter(e -> e.getLastName().length() >= 5) // needed to test startsWith/endsWith/contains operators
        .filter(e -> e.getBirthDate() != null)
        .filter(e -> e.getChildren() > 0)
        .filter(e -> !e.getVisitedLocations().isEmpty())
        .filter(e -> !e.getVisitedLocations().get(0).getCities().isEmpty())
        .filter(e -> !e.getOrders().isEmpty())
        .skip(skip).limit(1).findAny()
        .orElseThrow(() -> new IllegalStateException("No entities found"));
  }

  /**
   * Override this method if the database sorts the strings differently
   */
  protected <C> Comparator<C> getStringComparator(Function<C, String> extractValueFunction) {
    return Comparator.comparing(extractValueFunction, getSortComparator());
  }

  /**
   * Override this method if the database doesn't support handling null values in sorting
   */
  protected <C extends Comparable<? super C>> Comparator<C> getSortComparator() {
    return Comparator.nullsLast(Comparator.naturalOrder());
  }
}
