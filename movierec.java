import java.util.*;

class Movie{
    String title;
    List<String> genres;
    Map<String, Double> ratings;

    public Movie(String title, List<String> genres) {
        this.title = title;
        this.genres = genres;
        this.ratings = new HashMap<>();
    }

    public void addRating(String username, double rating) {
        ratings.put(username, rating);
    }

    public double getAverageRating() {
        double sum = 0.0;
        for (double rating : ratings.values()) {
            sum += rating;
        }
        return sum / ratings.size();
    }
}

class User {
    String username;
    Map<String, Double> preferences;
    Map<String, Double> ratings;

    public User(String username) {
        this.username = username;
        this.preferences = new HashMap<>();
        this.ratings = new HashMap<>();
    }

    public void updatePreference(String genre, double weight) {
        preferences.put(genre, weight);
    }

    public void rateMovie(String movieTitle, double rating) {
        ratings.put(movieTitle, rating);
    }
}

class MovieRecommendationSystem {
    List<Movie> movies;
    Map<String, User> users;

    public MovieRecommendationSystem() {
        this.movies = new ArrayList<>();
        this.users = new HashMap<>();
    }

    public void addMovie(String title, List<String> genres) {
        movies.add(new Movie(title, genres));
    }

    public void registerUser(String username) {
        users.put(username, new User(username));
    }

    public void rateMovie(String username, String movieTitle, double rating) {
        User user = users.get(username);
        if (user != null) {
            user.rateMovie(movieTitle, rating);
        }
    }

    public List<Movie> getRecommendedMovies(String username) {
        User user = users.get(username);
        if (user == null) {
            return new ArrayList<>();
        }

        // Collaborative filtering recommendation algorithm
        Map<Movie, Double> movieScores = new HashMap<>();
        for (Movie movie : movies) {
            double score = 0.0;
            int count = 0;
            for (Map.Entry<String, Double> entry : movie.ratings.entrySet()) {
                User otherUser = users.get(entry.getKey());
                if (otherUser != null) {
                    Double otherRating = otherUser.ratings.get(movie.title);
                    if (otherRating != null) {
                        score += entry.getValue() * otherRating;
                        count++;
                    }
                }
            }
            if (count > 0) {
                movieScores.put(movie, score / count);
            }
        }

        List<Movie> recommendedMovies = new ArrayList<>();
        movieScores.entrySet().stream()
            .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
            .limit(5)
            .forEach(entry -> recommendedMovies.add(entry.getKey()));

        return recommendedMovies;
    }

    public List<Movie> searchMoviesByGenre(String genre) {
        List<Movie> result = new ArrayList<>();
        for (Movie movie : movies) {
            if (movie.genres.contains(genre)) {
                result.add(movie);
            }
        }
        return result;
    }
}

public class movierec {
    public static void main(String[] args) {
        MovieRecommendationSystem recommendationSystem = new MovieRecommendationSystem();

        // Add movies and user preferences
        recommendationSystem.addMovie("Movie A", Arrays.asList("Action", "Adventure"));
        recommendationSystem.addMovie("Movie B", Arrays.asList("Comedy", "Romance"));
        recommendationSystem.addMovie("Movie C", Arrays.asList("Action", "Romance"));
        recommendationSystem.addMovie("Movie D", Arrays.asList("Horror", "Thriller"));
        recommendationSystem.addMovie("Movie E", Arrays.asList("Comedy", "Adventure"));

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        recommendationSystem.registerUser(username);

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Rate a movie");
            System.out.println("2. Get recommended movies");
            System.out.println("3. Search movies by genre");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter the movie title you want to rate: ");
                    String movieTitle = scanner.nextLine();
                    System.out.print("Enter your rating (0.0 to 5.0): ");
                    double rating = scanner.nextDouble();
                    recommendationSystem.rateMovie(username, movieTitle, rating);
                    System.out.println("Movie rated successfully!");
                    break;

                case 2:
                    List<Movie> recommendedMovies = recommendationSystem.getRecommendedMovies(username);
                    System.out.println("Recommended Movies:");
                    for (Movie movie : recommendedMovies) {
                        System.out.println(movie.title + " (Average Rating: " + movie.getAverageRating() + ")");
                    }
                    break;

                case 3:
                    System.out.print("Enter the genre you want to search: ");
                    String genre = scanner.nextLine();
                    List<Movie> genreMovies = recommendationSystem.searchMoviesByGenre(genre);
                    System.out.println("Movies in " + genre + " genre:");
                    for (Movie movie : genreMovies) {
                        System.out.println(movie.title);
                    }
                    break;

                case 4:
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}