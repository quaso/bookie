package org.bookie.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.transaction.Transactional;

import org.bookie.model.Booking;
import org.bookie.model.Organization;
import org.bookie.model.OrganizationUserRole;
import org.bookie.model.Place;
import org.bookie.model.Role;
import org.bookie.model.Season;
import org.bookie.model.SeasonPlace;
import org.bookie.model.User;
import org.bookie.repository.BookingRepositoryCustom;
import org.bookie.repository.OrganizationRepository;
import org.bookie.repository.OrganizationUserRoleRepository;
import org.bookie.repository.PlaceRepository;
import org.bookie.repository.RoleRepository;
import org.bookie.repository.SeasonPlaceRepository;
import org.bookie.repository.SeasonRepository;
import org.bookie.service.UserService;
import org.bookie.test.conf.TestConfiguration;
import org.bookie.util.password.PasswordPolicyException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles("test")
@Transactional
public abstract class AbstractTest {

	protected static final String USER_PWD = "pwd";

	@Autowired
	private BookingRepositoryCustom bookingRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private SeasonPlaceRepository seasonPlaceRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private OrganizationUserRoleRepository organizationUserRoleRepository;

	protected final Organization createOrganization(final String name) {
		final Organization org = new Organization();
		org.setName(name);
		org.setCode(name);
		return this.organizationRepository.save(org);
	}

	protected final Season createSeason(final Date start, final Date end, final String name,
			final Organization organization) {
		final Season season = new Season();
		season.setDateStart(start);
		season.setDateEnd(end);
		season.setTimeStart(7 * 60);
		season.setTimeEnd(22 * 60);
		season.setName(name);
		season.setOrganization(organization);
		return this.seasonRepository.save(season);
	}

	protected final void createSeasonPlace(final Season season, final Place place) {
		final SeasonPlace sp = new SeasonPlace();
		sp.setSeason(season);
		sp.setPlace(place);
		this.seasonPlaceRepository.save(sp);
	}

	protected final Place createPlace(final String name, final String type, final Organization organization) {
		final Place place = new Place();
		place.setName(name);
		place.setType(type);
		place.setOrganization(organization);
		return this.placeRepository.save(place);
	}

	protected final Booking createBooking(final LocalDateTime start, final LocalDateTime end, final String type,
			final User user, final Season season, final Place place) {
		final Booking booking = new Booking();
		booking.setTimeStart(this.date(start));
		booking.setTimeEnd(this.date(end));
		booking.setType(type);
		booking.setSeason(season);
		booking.setCreatedAt(new Date());
		booking.setPlace(place);
		booking.setOwner(user);
		booking.setCreatedBy(user);
		return this.bookingRepository.save(booking);
	}

	protected final Role createRole(final String name) {
		final Role role = new Role();
		role.setName(name);
		return this.roleRepository.save(role);
	}

	protected final User createUser(final String username, final Role role, final Organization organization) {
		final User user = new User();
		user.setUsername(username);
		user.setName("n" + username);
		user.setSurname("surname");
		user.setPhone("123");
		user.setPassword(USER_PWD);
		user.setEnabled(true);
		try {
			this.userService.createUser(user);
		} catch (final PasswordPolicyException e) {
		}

		final OrganizationUserRole our = new OrganizationUserRole();
		our.setValues(organization, user, role);
		this.organizationUserRoleRepository.save(our);

		return user;
	}

	protected final Date date(final LocalDateTime ldt) {
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}

	protected final Date date(final LocalDate ld) {
		return Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

}
