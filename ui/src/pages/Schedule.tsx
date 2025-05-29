import axios from 'axios';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { DragDropContext, Droppable, Draggable, DropResult, DroppableProvided, DraggableProvided } from '@hello-pangea/dnd';

interface Task {
  id: string;
  name: string;
}

interface Column {
  id: string;
  title: string;
  tasks: Task[];
}

export default function Schedule() {
  const queryClient = useQueryClient();

  // Fetch columns data
  const { data: columns = [], isLoading } = useQuery<Column[]>({
    queryKey: ['columns'],
    queryFn: async () => {
      const response = await axios.get('/api/columns');
      return response.data;
    },
  });

  // Update columns data
  const mutation = useMutation<void, Error, Column[]>(
    {
      mutationFn: async (updatedColumns) => {
        await axios.patch('/api/columns', updatedColumns);
      },
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ['columns'] });
      },
    }
  );

  const handleDragEnd = (result: DropResult) => {
    if (!result.destination) return;

    const sourceIndex = result.source.index;
    const destinationIndex = result.destination.index;
    const sourceColumnId = result.source.droppableId;
    const destinationColumnId = result.destination.droppableId;

    const updatedColumns = columns.map((col) => ({ ...col }));
    const sourceColumn = updatedColumns.find((col) => col.id === sourceColumnId);
    const destinationColumn = updatedColumns.find((col) => col.id === destinationColumnId);

    if (sourceColumn && destinationColumn) {
      const [movedTask] = sourceColumn.tasks.splice(sourceIndex, 1);
      destinationColumn.tasks.splice(destinationIndex, 0, movedTask);

      mutation.mutate(updatedColumns);
    }
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <DragDropContext onDragEnd={handleDragEnd}>
      <div className="kanban-board">
        {columns.map((column) => (
          <Droppable droppableId={column.id} key={column.id}>
            {(provided: DroppableProvided) => (
              <div
                {...provided.droppableProps}
                ref={provided.innerRef}
                className="kanban-column"
              >
                <h2>{column.title}</h2>
                {column.tasks.map((task, index) => (
                  <Draggable draggableId={task.id} index={index} key={task.id}>
                    {(provided: DraggableProvided) => (
                      <div
                        {...provided.draggableProps}
                        {...provided.dragHandleProps}
                        ref={provided.innerRef}
                        className="kanban-task"
                      >
                        <p>{task.name}</p>
                      </div>
                    )}
                  </Draggable>
                ))}
                {provided.placeholder}
              </div>
            )}
          </Droppable>
        ))}
      </div>
    </DragDropContext>
  );
}
