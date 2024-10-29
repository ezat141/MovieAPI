package com.movie.movieapi.service;

import com.movie.movieapi.dto.MovieDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface MovieService {
    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;
    MovieDto getMovie(Integer movieId);
    List<MovieDto> getAllMovies();
    MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException;
    String deleteMovie(Integer movieId) throws IOException;
}