UPDATE user_plot
SET plot_name = UPPER(plot_name)
WHERE plot_name IS NOT NULL
  AND plot_name <> UPPER(plot_name);
