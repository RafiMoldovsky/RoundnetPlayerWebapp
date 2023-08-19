'use client'
import { useState } from "react"
import { players, playerInterface } from "../../../data/players";
import Link from "next/link";

export default function PlayersPage(){
  // state to control player search value
  const [searchInput, setSearchInput] = useState('');

  //Making an array of the players that should show up on this search
  const searchedPlayers: playerInterface[] = [];

  for (var i in players) {
    if(players[i].name.toLowerCase().includes(searchInput.toLowerCase())){
      searchedPlayers.push(players[i]);
    }
  }
  
  return(
    <div className="flex flex-col items-center">
      <div>Players</div>
      <input className="border border-black" placeholder="Enter Player's Name" value={searchInput} onChange={(e) => setSearchInput(e.target.value)}/>
      {searchedPlayers.map(player => 
        <Link key={player.id} href={{
          pathname: `/players/${player.name}`,
          query: {playerData: JSON.stringify({name: player.name, id: player.id})}
        }}>
          <div className="text-gray-700 hover:text-black">
            {player.name}
          </div>
        </Link>
      )}
    </div>
  )
}