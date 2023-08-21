export const players:Map<string, playerInterface> = new Map([
  ["1", {
    id: "1",
    player_name: 'Rafi Moldovsky',
    games_won: 10,
    games_lost: 0,
    series_won: 5,
    series_lost: 0,
    points_won: 1000,
    points_lost: 20,
  }],
  ["2", {
    id: "2",
    player_name: 'Kyan Kornfeld',
    games_won: 0,
    games_lost: 9,
    series_won: 0,
    series_lost: 2,
    points_won: 10,
    points_lost: 334,
  }],
  ["3", {
    id: "3",
    player_name: 'William Foote',
    games_won: 4,
    games_lost: 5,
    series_won: 6,
    series_lost: 7,
    points_won: 800,
    points_lost: 900,
  }], 
  ["4", {
    id: "4",
    player_name: 'Arrow Griner',
    games_won: 10,
    games_lost: 3,
    series_won: 5,
    series_lost: 2,
    points_won: 600,
    points_lost: 100,
  }], 
  ["5", {
    id: "5",
    player_name: 'Cory Chilton',
    games_won: 8,
    games_lost: 9,
    series_won: 1,
    series_lost: 2,
    points_won: 323,
    points_lost: 334,
  }],
]);

export interface playerInterface {
  id: string;
  player_name: string;
  games_won: number;
  games_lost: number;
  series_won: number;
  series_lost: number;
  points_won: number;
  points_lost: number;
}

export function isAPlayer(obj: any): obj is playerInterface {
  if (typeof obj === 'undefined'){return false};
  return 'id' in obj && 'player_name' in obj && 'games_won' in obj && 'games_lost' in obj && 'series_won' in obj && 'series_lost' in obj && 'points_won' in obj && 'points_lost' in obj;
}